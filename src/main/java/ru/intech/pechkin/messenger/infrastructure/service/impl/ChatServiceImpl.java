package ru.intech.pechkin.messenger.infrastructure.service.impl;

import jakarta.validation.Valid;
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
import ru.intech.pechkin.messenger.infrastructure.service.mapper.MessengerServiceMapper;

import java.util.*;
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
//        TODO: Сделать метод в этом классе-сервисе для дополнения списка чатов в случае наличия у юзера роли супервизора,
//         тут же реализовать вызов его через if(employee.getSuperuser) departmentService.getCorporateChats()

        Page<Message> lastMessages = messageRepository.findLatestMessagesByChatIdIn(
                userRoleMutedPinnedChatRepository.findAllByUserId(dto.getUserId()),
                PageRequest.of(dto.getPageNumber(), dto.getPageSize())
        );
        List<UUID> chatIds = lastMessages.stream()
                .map(Message::getChatId)
                .toList();
        List<ChatDto> chatDtos = getChatDtoListByChatIdIn(chatIds);

        List<UserChatCheckedMessage> userChatCheckedMessages =
                userChatCheckedMessageRepository.findAllByUserIdAndChatIdInAndMessageIdIn(
                        dto.getUserId(),
                        chatIds,
                        lastMessages.stream()
                                .map(Message::getId)
                                .toList()
                );
        chatDtos.parallelStream().forEach(chatDto -> processChatDto(
                chatDto,
                lastMessages,
                userChatCheckedMessages,
                dto.getUserId()
        ));
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
                List.of(
                        userChatCheckedMessageRepository.findByUserIdAndChatIdAndMessageId(
                                dto.getUserId(),
                                lastMessage.getChatId(),
                                lastMessage.getId()
                        ).orElseThrow(NullPointerException::new)
                ),
                dto.getUserId()
        );
        return chatDto;
    }

    @Override
    public ChatDto getP2PChatByUsers(GetP2PChatByUsersDto dto) {
        List<UserRoleMutedPinnedChat> userRoleMutedPinnedChats =
                checkIfUsersHaveP2PChat(List.of(dto.getUserId(), dto.getSearchedUserId()));
        if (userRoleMutedPinnedChats.size() == 2) {
            return getChatByIdAndUserId(new GetChatByIdAndUserIdDto(
                    userRoleMutedPinnedChats.get(0).getChatId(),
                    dto.getUserId()
            ));
        } else {
            throw new IllegalArgumentException("P2P chat with this user does not exist");
        }
    }

    private void processChatDto(ChatDto chatDto, Page<Message> lastMessages, List<UserChatCheckedMessage> userChatCheckedMessages, UUID dto) {
        List<UserRoleMutedPinnedChat> userRoleMutedPinnedChats =
                userRoleMutedPinnedChatRepository.findAllByChatId(chatDto.getId());
        List<User> users = userRepository.findAllById(
                userRoleMutedPinnedChats.stream()
                        .map(UserRoleMutedPinnedChat::getUserId)
                        .toList()
        );
        setLastMessageInChatDto(chatDto, lastMessages.getContent(), users, userChatCheckedMessages);

        setUsersWithRolesInChatDto(chatDto, userRoleMutedPinnedChats, users);

        if (chatDto.getChatType().equals(ChatType.P2P)) {
            setTitleAndIconInP2PChatByInterlocutor(dto, chatDto, users);
        }

        setUnreadMessagesCountInChatDtoByUserId(dto, chatDto);

        setMutedStatusAndPinnedStatusInChatDtoByUserId(dto, chatDto, userRoleMutedPinnedChats);
    }

    @NonNull
    private List<ChatDto> getChatDtoListByChatIdIn(List<UUID> chatIds) {
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
            List<User> users,
            List<UserChatCheckedMessage> userChatCheckedMessages
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
                userChatCheckedMessages.stream()
                        .filter(userChatCheckedMessage ->
                                userChatCheckedMessage.getChatId().equals(chatDto.getId())
                                        && userChatCheckedMessage.getMessageId().equals(message.getId())
                        )
                        .findFirst()
                        .orElseThrow(NullPointerException::new)
                        .getChecked()
        ));
    }

    private void setUsersWithRolesInChatDto(
            ChatDto chatDto,
            List<UserRoleMutedPinnedChat> userRoleMutedPinnedChats,
            List<User> users
    ) {
        chatDto.setUsersWithRole(userRoleMutedPinnedChats.stream()
                .map(userRoleMutedPinnedChat -> users.stream()
                        .filter(user -> user.getId().equals(userRoleMutedPinnedChat.getUserId()))
                        .findFirst()
                        .orElseThrow(NullPointerException::new)
                )
                .map(user -> mapper.userAndRoleToUserWithRoleDto(
                        user,
                        userRoleMutedPinnedChats.stream()
                                .filter(userRoleMutedPinnedChat ->
                                        userRoleMutedPinnedChat.getUserId().equals(user.getId()))
                                .findFirst()
                                .orElseThrow(NullPointerException::new)
                                .getUserRole()))
                .toList());
    }

    private void setTitleAndIconInP2PChatByInterlocutor(
            UUID userId,
            ChatDto chatDto,
            List<User> users
    ) {
        User otherUser = users.parallelStream()
                .filter(user -> !user.getId().equals(userId))
                .findFirst()
                .orElseThrow(NullPointerException::new);
        chatDto.setTitle(otherUser.getUsername());
        chatDto.setIcon(otherUser.getIcon());
    }

    private void setUnreadMessagesCountInChatDtoByUserId(UUID userId, ChatDto chatDto) {
        int number = 0;
        long count = 0;
        Page<UserChatCheckedMessage> page;
        do {
            page = userChatCheckedMessageRepository.findAllByUserIdAndChatIdAndChecked(
                    userId,
                    chatDto.getId(),
                    false,
                    PageRequest.of(number, 2_000)
            );
            count += messageRepository.findAllByChatIdAndPublisherNot(chatDto.getId(), userId)
                    .parallelStream()
                    .count();
            number++;
        } while (!page.isLast());
        chatDto.setUnreadMessagesCount(count);
    }

    private void setMutedStatusAndPinnedStatusInChatDtoByUserId(
            UUID userId,
            ChatDto chatDto,
            List<UserRoleMutedPinnedChat> userRoleMutedPinnedChats
    ) {
        UserRoleMutedPinnedChat userRoleMutedPinnedChat = userRoleMutedPinnedChats.parallelStream()
                .filter(lambdaUserRoleMutedPinnedChat ->
                        lambdaUserRoleMutedPinnedChat.getUserId().equals(userId)
                                && lambdaUserRoleMutedPinnedChat.getChatId().equals(chatDto.getId())
                )
                .findFirst()
                .orElseThrow(NullPointerException::new);
        chatDto.setMuted(userRoleMutedPinnedChat.getMuted());
        chatDto.setPinned(userRoleMutedPinnedChat.getPinned());
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
    public ChatDto createFavoritesChat(UUID userId) {
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
        if (dto.getUsers().size() != 2 ||
                userRepository.findAllById(dto.getUsers())
                        .size() != 2) {
            throw new IllegalArgumentException("There must only be two users in a P2P chat");
        } else if (dto.getMessageDto() == null) {
            throw new IllegalArgumentException("To create P2P chat you need to send first message");
        }
        if (checkIfUsersHaveP2PChat(dto.getUsers()).size() == 2) {
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

    private List<UserRoleMutedPinnedChat> checkIfUsersHaveP2PChat(List<UUID> userIds) {
        List<UserRoleMutedPinnedChat> userRoleMutedPinnedChats =
                userRoleMutedPinnedChatRepository.findByUserIdInAndChatType(userIds, ChatType.P2P);
        return userRoleMutedPinnedChats.parallelStream()
                .filter(chat -> userRoleMutedPinnedChats.parallelStream()
                        .anyMatch(otherChat ->
                                chat.getChatId().equals(otherChat.getChatId())
                                        && !chat.getUserId().equals(otherChat.getUserId())
                        )
                )
                .toList();
    }

    @Override
    public ChatDto createGroupChat(CreateGroupChatDto dto) {
        if (!dto.getCorporate()
                && (dto.getUsers().isEmpty()
                || userRepository.findAllById(dto.getUsers().keySet()).isEmpty())) {
            throw new IllegalArgumentException("There must be users in the group chat");
        }
        Chat chat = Chat.createGroup(
                dto.getTitle(), dto.getIcon(), dto.getCorporate(), dto.getDepartmentId()
        );
        if (dto.getUsers() != null) {
            dto.getUsers().forEach((userId, userRole) ->
                    createAndSaveNewUserRoleMutedPinnedChat(userId, chat.getId(), userRole));
        }
        chatRepository.save(chat);
        return getChatDto(chat, sendSystemMessage(chat.getId(), "Chat is created"), dto.getUsers());
    }

    @NonNull
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
    public ChatDto updateGroupChat(UpdateGroupChatDto dto) {
        Chat chat = chatRepository.findById(dto.getChatId())
                .orElseThrow(NullPointerException::new);
        List<UserRoleMutedPinnedChat> userRoleMutedPinnedChats =
                userRoleMutedPinnedChatRepository.findAllByChatId(dto.getChatId());
        if (dto.getTitle() != null && dto.getIcon() != null) {
            chat.setTitle(dto.getTitle());
            chat.setIcon(dto.getIcon());
        }
        chatRepository.save(chat);

        List<User> users = getUsersForUpdatingGroupChat(dto, userRoleMutedPinnedChats);
        removeUsersWithRolesFromGroupChat(dto, userRoleMutedPinnedChats, users);
        addOrUpdateUsersWithRolesInGroupChat(dto, userRoleMutedPinnedChats, users);
        userRoleMutedPinnedChatRepository.saveAll(userRoleMutedPinnedChats);

        return getChatDto(chat, null, dto.getUsers());
    }

    private List<User> getUsersForUpdatingGroupChat(
            UpdateGroupChatDto dto,
            List<UserRoleMutedPinnedChat> userRoleMutedPinnedChats
    ) {
        List<UUID> userIds = new ArrayList<>(
                dto.getUsers()
                        .keySet()
                        .stream()
                        .toList()
        );
        userIds.addAll(
                userRoleMutedPinnedChats.stream()
                        .map(UserRoleMutedPinnedChat::getUserId)
                        .toList()
        );
        return userRepository.findAllById(userIds);
    }

    private void removeUsersWithRolesFromGroupChat(
            UpdateGroupChatDto dto,
            List<UserRoleMutedPinnedChat> userRoleMutedPinnedChats,
            List<User> users
    ) {
        userRoleMutedPinnedChats.forEach(userRoleMutedPinnedChat -> {
            Map.Entry<UUID, Role> filteredDto = dto.getUsers()
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getKey().equals(userRoleMutedPinnedChat.getUserId()))
                    .findFirst()
                    .orElse(null);
            if (filteredDto == null) {
                userRoleMutedPinnedChatRepository.deleteByUserId(userRoleMutedPinnedChat.getUserId());
                sendSystemMessage(
                        dto.getChatId(),
                        "@" + users.stream()
                                .filter(user -> user.getId().equals(userRoleMutedPinnedChat.getUserId()))
                                .findFirst()
                                .orElseThrow(NullPointerException::new)
                                .getUsername()
                                + " left the group"
                );
            }
        });
        userRoleMutedPinnedChats.removeIf(userRoleMutedPinnedChat -> !dto.getUsers().containsKey(userRoleMutedPinnedChat.getUserId()));
    }

    private void addOrUpdateUsersWithRolesInGroupChat(
            UpdateGroupChatDto dto,
            List<UserRoleMutedPinnedChat> userRoleMutedPinnedChats,
            List<User> users
    ) {
        dto.getUsers().forEach((userId, userRole) -> {
            UserRoleMutedPinnedChat filteredUserRoleMutedPinnedChat = userRoleMutedPinnedChats.stream()
                    .filter(userRoleMutedPinnedChat -> userId.equals(userRoleMutedPinnedChat.getUserId()))
                    .findFirst()
                    .orElse(null);
            if (filteredUserRoleMutedPinnedChat == null) {
                userRoleMutedPinnedChats.add(
                        UserRoleMutedPinnedChat.create(userId, dto.getChatId(), userRole)
                );
                sendSystemMessage(
                        dto.getChatId(),
                        "@" + users.stream()
                                .filter(user -> user.getId().equals(userId))
                                .findFirst()
                                .orElseThrow(NullPointerException::new)
                                .getUsername()
                                + " joined the group"
                );
            } else {
                userRoleMutedPinnedChats.set(
                        userRoleMutedPinnedChats.indexOf(filteredUserRoleMutedPinnedChat),
                        UserRoleMutedPinnedChat.builder()
                                .id(filteredUserRoleMutedPinnedChat.getId())
                                .chatId(filteredUserRoleMutedPinnedChat.getChatId())
                                .userId(userId)
                                .userRole(userRole)
                                .muted(filteredUserRoleMutedPinnedChat.getMuted())
                                .pinned(filteredUserRoleMutedPinnedChat.getPinned())
                                .build()
                );
            }
        });
    }

    @Override
    public void updateChatMutedStatus(UpdateChatMutedOrPinnedStatusDto dto) {
        UserRoleMutedPinnedChat userRoleMutedPinnedChat =
                userRoleMutedPinnedChatRepository.findByUserIdAndChatId(dto.getUserId(), dto.getChatId());
        userRoleMutedPinnedChat.setMuted(dto.getStatus());
        userRoleMutedPinnedChatRepository.save(userRoleMutedPinnedChat);
    }

    @Override
    public void updateChatPinnedStatus(UpdateChatMutedOrPinnedStatusDto dto) {
        UserRoleMutedPinnedChat userRoleMutedPinnedChat =
                userRoleMutedPinnedChatRepository.findByUserIdAndChatId(dto.getUserId(), dto.getChatId());
        userRoleMutedPinnedChat.setPinned(dto.getStatus());
        userRoleMutedPinnedChatRepository.save(userRoleMutedPinnedChat);
    }

    @Override
    public List<UUID> deleteChat(UUID chatId) {
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
                    .findAllByChatId(chatId, PageRequest.of(0, 100))
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
