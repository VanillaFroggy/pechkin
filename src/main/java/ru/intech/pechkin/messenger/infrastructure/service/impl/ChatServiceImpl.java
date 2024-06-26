package ru.intech.pechkin.messenger.infrastructure.service.impl;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.*;
import ru.intech.pechkin.messenger.infrastructure.persistence.repo.*;
import ru.intech.pechkin.messenger.infrastructure.service.ChatService;
import ru.intech.pechkin.messenger.infrastructure.service.MessageService;
import ru.intech.pechkin.messenger.infrastructure.service.dto.chat.*;
import ru.intech.pechkin.messenger.infrastructure.service.dto.message.MessageDataDto;
import ru.intech.pechkin.messenger.infrastructure.service.dto.message.MessageDto;
import ru.intech.pechkin.messenger.infrastructure.service.dto.message.SendMessageDto;
import ru.intech.pechkin.messenger.infrastructure.service.dto.user.UserWithRoleDto;
import ru.intech.pechkin.messenger.infrastructure.service.mapper.MessengerServiceMapper;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatRepository chatRepository;
    private final UserRoleMutedPinnedChatRepository userRoleMutedPinnedChatRepository;
    private final MessageRepository messageRepository;
    private final MessageDataRepository messageDataRepository;
    private final UserRepository userRepository;
    private final ChatMessageDataMessageRepository chatMessageDataMessageRepository;
    private final UserChatCheckedMessageRepository userChatCheckedMessageRepository;
    private final MessageService messageService;
    private final MessengerServiceMapper mapper;

    @Override
    public Page<ChatDto> getPageOfChats(@Valid GetPageOfChatsDto dto) {
        Page<Message> lastMessages = messageRepository.findLatestMessagesByChatIdIn(
                userRoleMutedPinnedChatRepository.findAllByUserId(dto.getUserId()),
                PageRequest.of(dto.getPageNumber(), dto.getPageSize())
        );
        Set<UUID> chatIds = lastMessages.stream()
                .map(Message::getChatId)
                .collect(Collectors.toSet());
        List<ChatDto> chatDtos = getChatDtoListByChatIdIn(chatIds);

        Map<UUID, Boolean> userChatCheckedMessages =
                userChatCheckedMessageRepository.findAllByUserIdAndChatIdInAndMessageIdIn(
                                dto.getUserId(),
                                chatIds,
                                lastMessages.stream()
                                        .map(Message::getId)
                                        .collect(Collectors.toSet())
                        ).stream()
                        .collect(Collectors.toMap(
                                UserChatCheckedMessage::getChatId,
                                UserChatCheckedMessage::getChecked
                        ));

        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<Void>> futures = chatDtos.stream()
                    .map(chatDto -> CompletableFuture.runAsync(() ->
                                    processChatDto(
                                            chatDto,
                                            lastMessages,
                                            userChatCheckedMessages,
                                            dto.getUserId(),
                                            false
                                    ),
                            executorService
                    ))
                    .toList();
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            executorService.shutdown();
        }

        return new PageImpl<>(
                sortChatDtoListByPinnedAndDateTime(chatDtos),
                lastMessages.getPageable(),
                lastMessages.getTotalElements()
        );
    }

    @Override
    public ChatDto getChatByIdAndUserId(GetChatByIdAndUserIdDto dto) {
        Message lastMessage = messageRepository.findFirstByChatIdOrderByDateTimeDesc(dto.getChatId())
                .orElseThrow(NullPointerException::new);
        ChatDto chatDto = mapper.chatToChatDto(
                chatRepository.findById(lastMessage.getChatId())
                        .orElseThrow(NullPointerException::new)
        );
        processChatDto(
                chatDto,
                new PageImpl<>(List.of(lastMessage)),
                Map.of(
                        chatDto.getId(),
                        userChatCheckedMessageRepository.findByUserIdAndChatIdAndMessageId(
                                        dto.getUserId(),
                                        lastMessage.getChatId(),
                                        lastMessage.getId()
                                ).orElseThrow(NullPointerException::new)
                                .getChecked()
                ),
                dto.getUserId(),
                true
        );
        return chatDto;
    }

    private void processChatDto(
            ChatDto chatDto,
            Page<Message> lastMessages,
            Map<UUID, Boolean> userChatCheckedMessages,
            UUID userId,
            boolean onlyOneChat
    ) {
        Map<UUID, UserRoleMutedPinnedChat> userRoleMutedPinnedChats =
                userRoleMutedPinnedChatRepository.findAllByChatId(chatDto.getId())
                        .stream()
                        .collect(Collectors.toMap(
                                UserRoleMutedPinnedChat::getUserId,
                                userRoleMutedPinnedChat -> userRoleMutedPinnedChat
                        ));
        Map<UUID, User> users = userRepository.findAllById(userRoleMutedPinnedChats.keySet())
                .stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        setLastMessageInChatDto(chatDto, lastMessages.getContent(), users, userChatCheckedMessages);

        if (onlyOneChat) {
            setUsersWithRolesInChatDto(chatDto, userRoleMutedPinnedChats, users);
        }

        if (chatDto.getChatType().equals(ChatType.P2P)) {
            setTitleAndIconInP2PChatByInterlocutor(userId, chatDto, users);
        }

        setUnreadMessagesCountInChatDtoByUserId(userId, chatDto);

        setMutedStatusAndPinnedStatusInChatDtoByUserId(userId, chatDto, userRoleMutedPinnedChats);
    }

    @NonNull
    private List<ChatDto> getChatDtoListByChatIdIn(Set<UUID> chatIds) {
        List<Chat> chats = chatRepository.findAllById(chatIds);
        if (chats.isEmpty()) {
            throw new NoSuchElementException("There is no chats for you");
        }
        return chats.stream()
                .map(mapper::chatToChatDto)
                .toList();
    }

    private void setLastMessageInChatDto(
            ChatDto chatDto,
            List<Message> lastMessages,
            Map<UUID, User> users,
            Map<UUID, Boolean> userChatCheckedMessages
    ) {
        Message message = lastMessages.stream()
                .filter(lastMessage -> lastMessage.getChatId().equals(chatDto.getId()))
                .findFirst()
                .orElseThrow(NullPointerException::new);
        message.getDatas().forEach(messageData -> {
            if (messageData.getMessageType().equals(MessageType.TEXT) && messageData.getValue().length() > 50) {
                messageData.setValue(messageData.getValue().substring(0, 50));
            }
        });
        chatDto.setMessage(mapper.wrapMessageToMessageDto(
                message,
                users,
                userChatCheckedMessages.get(chatDto.getId())
        ));
    }

    private void setUsersWithRolesInChatDto(
            ChatDto chatDto,
            Map<UUID, UserRoleMutedPinnedChat> userRoleMutedPinnedChats,
            Map<UUID, User> users
    ) {
        chatDto.setUsersWithRole(
                userRoleMutedPinnedChats.entrySet().stream()
                        .map(userRoleMutedPinnedChat -> mapper.userAndRoleToUserWithRoleDto(
                                users.get(userRoleMutedPinnedChat.getKey()),
                                userRoleMutedPinnedChat.getValue().getUserRole())
                        )
                        .toList()
        );
    }

    private void setTitleAndIconInP2PChatByInterlocutor(
            UUID userId,
            ChatDto chatDto,
            Map<UUID, User> users
    ) {
        users.forEach((key, value) -> {
            if (!key.equals(userId)) {
                chatDto.setTitle(value.getUsername());
                chatDto.setIcon(value.getIcon());
            }
        });
    }

    private void setUnreadMessagesCountInChatDtoByUserId(UUID userId, ChatDto chatDto) {
        chatDto.setUnreadMessagesCount(
                messageRepository.findAllByUserIdChatIdAndPublisherNotAndChecked(
                        userId,
                        chatDto.getId(),
                        userId,
                        false,
                        PageRequest.of(0, 100)
                ).getTotalElements()
        );
    }

    private void setMutedStatusAndPinnedStatusInChatDtoByUserId(
            UUID userId,
            ChatDto chatDto,
            Map<UUID, UserRoleMutedPinnedChat> userRoleMutedPinnedChats
    ) {
        chatDto.setMuted(userRoleMutedPinnedChats.get(userId).getMuted());
        chatDto.setPinned(userRoleMutedPinnedChats.get(userId).getPinned());
    }

    @NonNull
    private List<ChatDto> sortChatDtoListByPinnedAndDateTime(List<ChatDto> chatDtos) {
        return chatDtos.stream()
                .sorted(
                        Comparator.comparing(ChatDto::getPinned)
                                .thenComparing(chatDto -> chatDto.getMessage().getDateTime())
                                .reversed()
                )
                .toList();
    }

    @Override
    public ChatDto getP2PChatByUsers(GetP2PChatByUsersDto dto) {
        Optional<List<UserRoleMutedPinnedChat>> userRoleMutedPinnedChats =
                checkIfUsersHaveP2PChat(List.of(dto.getUserId(), dto.getSearchedUserId()));
        if (userRoleMutedPinnedChats.isPresent()) {
            return getChatByIdAndUserId(new GetChatByIdAndUserIdDto(
                    userRoleMutedPinnedChats.get()
                            .getFirst()
                            .getChatId(),
                    dto.getUserId()
            ));
        } else {
            throw new IllegalArgumentException("P2P chat with this user does not exist");
        }
    }

    @Override
    public ChatDto createFavoritesChat(@NotNull UUID userId) {
        if (!userRoleMutedPinnedChatRepository.findByUserIdInAndChatType(List.of(userId), ChatType.FAVORITES).isEmpty()) {
            throw new IllegalArgumentException("Favorites chat for this user already exists");
        }
        Chat chat = Chat.createFavorites();
        chatRepository.save(chat);
        createAndSaveNewUserRoleMutedPinnedChat(userId, chat.getId(), Role.ADMIN);
        return getChatDto(
                chat,
                sendSystemMessage(chat.getId(), "Chat is created"),
                Map.of(userId, Role.ADMIN)
        );
    }

    @Override
    public ChatDto createP2PChat(@Valid CreateP2PChatDto dto) {
        if (userRepository.findAllById(dto.getUsers()).size() != 2) {
            throw new IllegalArgumentException("There must only be two users in a P2P chat");
        }
        if (checkIfUsersHaveP2PChat(dto.getUsers()).isPresent()) {
            throw new IllegalArgumentException("P2P chat with this user already exists");
        }
        Chat chat = Chat.createP2P();
        chatRepository.save(chat);
        dto.getUsers().forEach(userId -> createAndSaveNewUserRoleMutedPinnedChat(userId, chat.getId(), Role.ADMIN));
        return getChatDto(
                chat,
                messageService.sendMessage(
                        SendMessageDto.builder()
                                .chatId(chat.getId())
                                .userId(dto.getMessageDto().getPublisher())
                                .dataDtos(dto.getMessageDto().getDataDtos())
                                .build()
                ),
                dto.getUsers().stream()
                        .collect(Collectors.toMap(uuid -> uuid, value -> Role.ADMIN))
        );
    }

    private Optional<List<UserRoleMutedPinnedChat>> checkIfUsersHaveP2PChat(List<UUID> userIds) {
        return userRoleMutedPinnedChatRepository.findByUserIdInAndChatType(userIds, ChatType.P2P)
                .stream()
                .collect(Collectors.groupingBy(UserRoleMutedPinnedChat::getChatId))
                .values()
                .stream()
                .filter(userRoleMutedPinnedChats -> userRoleMutedPinnedChats.size() == 2)
                .findAny();
    }

    @Override
    public ChatDto createGroupChat(@Valid CreateGroupChatDto dto) {
        if (!dto.getCorporate()
                && (dto.getUsers().isEmpty()
                || userRepository.findAllById(dto.getUsers().keySet()).isEmpty())) {
            throw new IllegalArgumentException("There must be users in the group chat");
        }
        Chat chat = Chat.createGroup(
                dto.getTitle(),
                dto.getIcon(),
                dto.getCorporate(),
                dto.getDepartmentId()
        );
        if (dto.getUsers() != null) {
            dto.getUsers().forEach((userId, userRole) ->
                    createAndSaveNewUserRoleMutedPinnedChat(userId, chat.getId(), userRole));
        }
        chatRepository.save(chat);
        return getChatDto(chat, sendSystemMessage(chat.getId(), "Chat is created"), dto.getUsers());
    }

    private ChatDto getChatDto(Chat chat, MessageDto lastMessage, Map<UUID, Role> usersWithRole) {
        ChatDto chatDto = mapper.chatToChatDto(chat);
        chatDto.setMessage(lastMessage);
        if (usersWithRole != null) {
            List<User> users = userRepository.findAllById(usersWithRole.keySet());
            chatDto.setUsersWithRole(
                    users.stream()
                            .map(user -> mapper.userAndRoleToUserWithRoleDto(
                                    user,
                                    usersWithRole.get(user.getId())
                            ))
                            .toList()
            );
        }
        chatDto.setUnreadMessagesCount(1);
        chatDto.setMuted(false);
        chatDto.setPinned(false);
        return chatDto;
    }

    private void createAndSaveNewUserRoleMutedPinnedChat(UUID userId, UUID chatId, Role userRole) {
        userRoleMutedPinnedChatRepository.save(
                UserRoleMutedPinnedChat.create(userId, chatId, userRole)
        );
    }

    private MessageDto sendSystemMessage(UUID chatId, String value) {
        return messageService.sendMessage(
                SendMessageDto.builder()
                        .chatId(chatId)
                        .dataDtos(List.of(
                                new MessageDataDto(
                                        MessageType.TEXT,
                                        value
                                )
                        ))
                        .build()
        );
    }

    @Override
    public ChatDto updateGroupChat(@Valid UpdateGroupChatDto dto) {
        Chat chat = chatRepository.findById(dto.getChatId())
                .orElseThrow(NullPointerException::new);
        List<UserRoleMutedPinnedChat> userRoleMutedPinnedChats =
                userRoleMutedPinnedChatRepository.findAllByChatId(dto.getChatId());
            chat.setTitle(dto.getTitle());
            chat.setIcon(dto.getIcon());
        chatRepository.save(chat);

        Map<UUID, String> usersWithNames =
                getUsersWithNamesForUpdatingGroupChat(dto.getUsers(), userRoleMutedPinnedChats);
        removeUsersWithRolesFromGroupChat(dto, userRoleMutedPinnedChats, usersWithNames);
        addOrUpdateUsersWithRolesInGroupChat(dto, userRoleMutedPinnedChats, usersWithNames);

        return getChatDto(chat, null, dto.getUsers());
    }

    private Map<UUID, String> getUsersWithNamesForUpdatingGroupChat(
            Map<UUID, Role> usersWithRoles,
            List<UserRoleMutedPinnedChat> userRoleMutedPinnedChats
    ) {
        Set<UUID> set = new HashSet<>(usersWithRoles.keySet());
        set.addAll(
                userRoleMutedPinnedChats.stream()
                        .map(UserRoleMutedPinnedChat::getUserId)
                        .collect(Collectors.toSet())
        );
        return userRepository.findAllById(set)
                .stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));
    }

    private void removeUsersWithRolesFromGroupChat(
            UpdateGroupChatDto dto,
            List<UserRoleMutedPinnedChat> userRoleMutedPinnedChats,
            Map<UUID, String> usersWithNames
    ) {
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<Void>> futures = userRoleMutedPinnedChats.stream()
                    .map(userRoleMutedPinnedChat -> CompletableFuture.runAsync(() -> {
                                if (!dto.getUsers().containsKey(userRoleMutedPinnedChat.getUserId())) {
                                    removeUserWithUsernameFromGroupChat(
                                            dto.getChatId(),
                                            userRoleMutedPinnedChat.getUserId(),
                                            usersWithNames.get(userRoleMutedPinnedChat.getUserId())
                                    );
                                }
                            },
                            executorService
                    ))
                    .toList();
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            executorService.shutdown();
        }
        userRoleMutedPinnedChats.removeIf(userRoleMutedPinnedChat ->
                !dto.getUsers().containsKey(userRoleMutedPinnedChat.getUserId()));
    }

    private void removeUserWithUsernameFromGroupChat(UUID chatId, UUID userId, String username) {
        userRoleMutedPinnedChatRepository.deleteByUserIdAndChatId(userId, chatId);
        sendSystemMessage(
                chatId,
                "@" + username + " left the group"
        );
    }

    private void addOrUpdateUsersWithRolesInGroupChat(
            UpdateGroupChatDto dto,
            List<UserRoleMutedPinnedChat> userRoleMutedPinnedChats,
            Map<UUID, String> usersWithNames
    ) {
        Map<UUID, UserRoleMutedPinnedChat> uuidUserRoleMutedPinnedChatMap =
                userRoleMutedPinnedChats.stream()
                        .collect(Collectors.toMap(
                                UserRoleMutedPinnedChat::getUserId,
                                userRoleMutedPinnedChat -> userRoleMutedPinnedChat
                        ));
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<Void>> futures = dto.getUsers().entrySet().stream()
                    .map(entry -> CompletableFuture.runAsync(() -> {
                                if (!uuidUserRoleMutedPinnedChatMap.containsKey(entry.getKey())) {
                                    userRoleMutedPinnedChats.add(
                                            addUserWithRoleByUpdatingGroupChat(
                                                    dto.getChatId(),
                                                    usersWithNames.get(entry.getKey()),
                                                    entry.getKey(),
                                                    entry.getValue()
                                            )
                                    );
                                } else if (
                                        !uuidUserRoleMutedPinnedChatMap.get(entry.getKey())
                                                .getUserRole()
                                                .equals(entry.getValue())
                                ) {
                                    UserRoleMutedPinnedChat userRoleMutedPinnedChat =
                                            userRoleMutedPinnedChats.get(userRoleMutedPinnedChats.indexOf(
                                                    uuidUserRoleMutedPinnedChatMap.get(entry.getKey()))
                                            );
                                    userRoleMutedPinnedChat.setUserRole(entry.getValue());
                                    userRoleMutedPinnedChatRepository.save(userRoleMutedPinnedChat);
                                }
                            },
                            executorService
                    ))
                    .toList();
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            executorService.shutdown();
        }
    }

    private UserRoleMutedPinnedChat addUserWithRoleByUpdatingGroupChat(
            UUID chatId,
            String username,
            UUID userId,
            Role userRole
    ) {
        UserRoleMutedPinnedChat userRoleMutedPinnedChat = UserRoleMutedPinnedChat.create(userId, chatId, userRole);
        userRoleMutedPinnedChatRepository.save(userRoleMutedPinnedChat);
        sendSystemMessage(chatId, "@" + username + " joined the group");
        return userRoleMutedPinnedChat;
    }

    @Override
    public void updateGroupChatTitle(@Valid UpdateGroupChatTitleOrIconDto dto) {
        Chat chat = chatRepository.findById(dto.getChatId())
                .orElseThrow(NullPointerException::new);
        chat.setTitle(dto.getValue());
        chatRepository.save(chat);
    }

    @Override
    public void updateGroupChatIcon(@Valid UpdateGroupChatTitleOrIconDto dto) {
        Chat chat = chatRepository.findById(dto.getChatId())
                .orElseThrow(NullPointerException::new);
        chat.setIcon(dto.getValue());
        chatRepository.save(chat);
    }

    @Override
    public UserWithRoleDto addUserToGroupChat(@Valid UpdateUserInGroupChatDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(NullPointerException::new);
        if (userRoleMutedPinnedChatRepository.findByUserIdAndChatId(dto.getUserId(), dto.getChatId()) != null) {
            throw new IllegalArgumentException("This user is already in this chat");
        }
        addUserWithRoleByUpdatingGroupChat(
                dto.getChatId(),
                user.getUsername(),
                dto.getUserId(),
                dto.getUserRole()
        );
        return mapper.userAndRoleToUserWithRoleDto(user, dto.getUserRole());
    }

    @Override
    public UserWithRoleDto updateUserInGroupChat(@Valid UpdateUserInGroupChatDto dto) {
        UserRoleMutedPinnedChat userRoleMutedPinnedChat =
                userRoleMutedPinnedChatRepository.findByUserIdAndChatId(dto.getUserId(), dto.getChatId());
        userRoleMutedPinnedChat.setUserRole(dto.getUserRole());
        userRoleMutedPinnedChatRepository.save(userRoleMutedPinnedChat);
        return mapper.userAndRoleToUserWithRoleDto(
                userRepository.findById(dto.getUserId())
                        .orElseThrow(NullPointerException::new),
                dto.getUserRole()
        );
    }

    @Override
    public UserWithRoleDto removeUserFromGroupChat(@Valid UpdateUserInGroupChatDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(NullPointerException::new);
        if (userRoleMutedPinnedChatRepository.findByUserIdAndChatId(dto.getUserId(), dto.getChatId()) == null) {
            throw new IllegalArgumentException("This user is not in this chat");
        }
        removeUserWithUsernameFromGroupChat(
                dto.getChatId(),
                dto.getUserId(),
                user.getUsername()
        );
        return mapper.userAndRoleToUserWithRoleDto(user, dto.getUserRole());
    }

    @Override
    public void updateChatMutedStatus(@Valid UpdateChatMutedOrPinnedStatusDto dto) {
        UserRoleMutedPinnedChat userRoleMutedPinnedChat =
                userRoleMutedPinnedChatRepository.findByUserIdAndChatId(dto.getUserId(), dto.getChatId());
        userRoleMutedPinnedChat.setMuted(dto.getStatus());
        userRoleMutedPinnedChatRepository.save(userRoleMutedPinnedChat);
    }

    @Override
    public void updateChatPinnedStatus(@Valid UpdateChatMutedOrPinnedStatusDto dto) {
        UserRoleMutedPinnedChat userRoleMutedPinnedChat =
                userRoleMutedPinnedChatRepository.findByUserIdAndChatId(dto.getUserId(), dto.getChatId());
        userRoleMutedPinnedChat.setPinned(dto.getStatus());
        userRoleMutedPinnedChatRepository.save(userRoleMutedPinnedChat);
    }

    @Override
    public List<UUID> deleteChat(@NotNull UUID chatId) {
        chatRepository.findById(chatId).orElseThrow(NullPointerException::new);
        chatRepository.deleteById(chatId);
        List<UUID> userIds = userRoleMutedPinnedChatRepository.findAllByChatId(chatId)
                .stream()
                .map(UserRoleMutedPinnedChat::getUserId)
                .toList();
        userRoleMutedPinnedChatRepository.deleteAllByChatId(chatId);
        messageRepository.deleteAllByChatId(chatId);
        userChatCheckedMessageRepository.deleteAllByChatId(chatId);
        Page<ChatMessageDataMessage> page;
        do {
            page = chatMessageDataMessageRepository
                    .findAllByChatId(chatId, PageRequest.of(0, 1_000))
                    .orElseThrow(NullPointerException::new);
            messageDataRepository.deleteAllById(
                    page.getContent()
                            .stream()
                            .map(ChatMessageDataMessage::getMessageDataId)
                            .toList()
            );
            chatMessageDataMessageRepository.deleteAllByChatIdAndIdIn(
                    chatId,
                    page.getContent()
                            .stream()
                            .map(ChatMessageDataMessage::getId)
                            .toList()
            );
        } while (!page.isLast());
        return userIds;
    }
}
