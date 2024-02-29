package ru.intech.pechkin.messenger.infrastructure.service.impl;

import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.*;
import ru.intech.pechkin.messenger.infrastructure.persistance.repo.*;
import ru.intech.pechkin.messenger.infrastructure.service.ChatService;
import ru.intech.pechkin.messenger.infrastructure.service.MessageService;
import ru.intech.pechkin.messenger.infrastructure.service.dto.*;
import ru.intech.pechkin.messenger.infrastructure.service.mapper.MessengerServiceMapper;

import java.time.LocalDateTime;
import java.util.*;

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

    //    TODO: Разбить большие методы на множество малых + придумать, как можно посылать запрос в бд всего один раз,
    //     а не для каждого элемента списка + придумать, как можно реализовать подключения через WebSocket
    @Override
    public List<ChatDto> getAllChats(UUID userId) {
//        TODO: Сделать метод в этом классе-сервисе для дополнения списка чатов в случае наличия у юзера роли супервизора,
//         тут же реализовать вызов его через if(employee.getSuperuser) departmentService.getCorporateChats()
        List<ChatDto> chatDtos = getChatDtoListByUserId(userId);
        List<Message> lastMessages = messageRepository.findLatestMessagesByChatIdIn(
                chatDtos.stream()
                        .map(ChatDto::getId)
                        .toList()
        );
        chatDtos.forEach(chatDto -> {
            setLastMessageInChatDto(userId, chatDto, lastMessages);

            List<UserRoleMutedPinnedChat> userRoleMutedPinnedChats =
                    userRoleMutedPinnedChatRepository.findAllByChatId(chatDto.getId());
            setUsersWithRolesInChatDto(chatDto, userRoleMutedPinnedChats);

            if (chatDto.getChatType().equals(ChatType.P2P)) {
                setTitleAndIconInP2PChatByInterlocutor(userId, chatDto, userRoleMutedPinnedChats);
            }

            setUnreadMessagesCountInChatDtoByUserId(userId, chatDto);

            setMutedStatusAndPinnedStatusInChatDtoByUserId(userId, chatDto);
        });
        return sortChatDtoListByPinnedAndDateTime(chatDtos);
    }

    @NonNull
    private List<ChatDto> getChatDtoListByUserId(UUID userId) {
        List<Chat> chats = chatRepository.findAllByIdIn(
                userRoleMutedPinnedChatRepository.findAllByUserId(userId)
                        .stream()
                        .map(UserRoleMutedPinnedChat::getChatId)
                        .toList()
        );
        if (chats.isEmpty()) {
            throw new NoSuchElementException("Чаты для данного пользователя пока отстутствуют");
        }
        return chats.stream()
                .map(mapper::chatToChatDto)
                .toList();
    }

    private void setLastMessageInChatDto(UUID userId, ChatDto chatDto, List<Message> lastMessages) {
        Message message = lastMessages.stream()
                .filter(lastMessage -> lastMessage.getChatId().equals(chatDto.getId()))
                .findFirst()
                .orElseThrow(NullPointerException::new);
        message.getDatas().forEach(messageData -> {
            if (messageData.getMessageType().equals(MessageType.TEXT) && messageData.getValue().length() > 50) {
                messageData.setValue(messageData.getValue().substring(0, 50));
            }
        });
        MessagePublisherDto publisherDto = null;
        if (message.getPublisher() != null) {
            publisherDto = mapper.userToMessagePublisherDto(
                    userRepository.findById(message.getPublisher())
                            .orElseThrow(NullPointerException::new)
            );
        }
        chatDto.setMessage(mapper.messageToMessageDto(
                message,
                publisherDto,
                userChatCheckedMessageRepository
                        .findByUserIdAndChatIdAndMessageId(userId, chatDto.getId(), message.getId())
                        .orElseThrow(NullPointerException::new)
                        .getChecked()
        ));
    }

    private void setUsersWithRolesInChatDto(ChatDto chatDto, List<UserRoleMutedPinnedChat> userRoleMutedPinnedChats) {
        chatDto.setUsersWithRole(userRoleMutedPinnedChats.stream()
                .map(userRoleMutedPinnedChat -> userRepository.findById(userRoleMutedPinnedChat.getUserId())
                        .orElseThrow(NullPointerException::new))
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
            List<UserRoleMutedPinnedChat> userRoleMutedPinnedChats
    ) {
        User otherUser = userRepository.findById(
                userRoleMutedPinnedChats.stream()
                        .filter(userRoleMutedPinnedChat -> !userRoleMutedPinnedChat.getUserId().equals(userId))
                        .findFirst()
                        .orElseThrow(NullPointerException::new)
                        .getUserId()
        ).orElseThrow(NullPointerException::new);
        chatDto.setTitle(otherUser.getUsername());
        chatDto.setIcon(otherUser.getIcon());
    }

    private void setUnreadMessagesCountInChatDtoByUserId(UUID userId, ChatDto chatDto) {
        List<UserChatCheckedMessage> userChatCheckedMessages = userChatCheckedMessageRepository
                .findAllByUserIdAndChatIdAndChecked(userId, chatDto.getId(), false);
        chatDto.setUnreadMessagesCount(
                userChatCheckedMessages.stream()
                        .filter(userChatCheckedMessage -> !userChatCheckedMessage.getUserId()
                                .equals(
                                        messageRepository.findById(userChatCheckedMessage.getMessageId())
                                                .orElseThrow(NullPointerException::new)
                                                .getPublisher()
                                )
                        )
                        .count()
        );
    }

    private void setMutedStatusAndPinnedStatusInChatDtoByUserId(UUID userId, ChatDto chatDto) {
        UserRoleMutedPinnedChat userRoleMutedPinnedChat =
                userRoleMutedPinnedChatRepository.findByUserIdAndChatId(userId, chatDto.getId());
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
    public ChatCreationResponse createFavoritesChat(UUID userId) {
        Chat chat = Chat.createFavorites();
        chatRepository.save(chat);
        createAndSaveNewUserRoleMutedPinnedChat(userId, chat.getId(), Role.ADMIN);
        sendSystemMessage(chat.getId(), "Chat is created");
        return new ChatCreationResponse(chat.getId());
    }

    //    TODO: написать функционал добавления записи P2PChatUser в бд при создании P2P чата
    //     для дальнейшего использования в поисковой системе на моменте определения, есть у пользователей
    //     лс переписка, или нет. Будет идти поиск по этой сущности, её атрибуты: chatId, userIds[2]
    //     ...
    //     Можно сделать на фронте за счёт анализа JSON-а с чатами, если есть чат типа P2P с искомым человеком,
    //     то открываем чат, если нет, то открываем форму для создания P2P чата
    @Override
    public ChatCreationResponse createP2PChat(@Valid CreateP2PChatDto dto) {
        if (dto.getUsers().size() != 2 ||
                userRepository.findByIdIn(dto.getUsers())
                        .orElseThrow(NullPointerException::new)
                        .size() != 2) {
            throw new IllegalArgumentException("В приватном чате должно быть лишь два пользователя");
        } else if (dto.getMessageDto() == null) {
            throw new IllegalArgumentException("При создании в приватном чате должно быть первое сообщение");
        }
        Chat chat = Chat.createP2P();
        chatRepository.save(chat);
        dto.getUsers().forEach(userId -> createAndSaveNewUserRoleMutedPinnedChat(userId, chat.getId(), Role.ADMIN));
        messageService.sendMessage(
                SendMessageDto.builder()
                        .chatId(chat.getId())
                        .userId(dto.getMessageDto().getPublisher())
                        .dataDtos(dto.getMessageDto().getDataDtos())
                        .dateTime(dto.getMessageDto().getDateTime())
                        .build()
        );
        return new ChatCreationResponse(chat.getId());
    }

    @Override
    public ChatCreationResponse createGroupChat(CreateGroupChatDto dto) {
        if (dto.getUsers().isEmpty() ||
                userRepository.findByIdIn(dto.getUsers().keySet().stream().toList())
                        .orElseThrow(NullPointerException::new).isEmpty()) {
            throw new IllegalArgumentException("В групповом чате должны быть пользователи");
        }
        Chat chat = Chat.createGroup(
                dto.getTitle(), dto.getIcon(), dto.getCorporate(), dto.getDepartmentId()
        );
        dto.getUsers().forEach((userId, userRole) ->
                createAndSaveNewUserRoleMutedPinnedChat(userId, chat.getId(), userRole));
        chatRepository.save(chat);
        sendSystemMessage(chat.getId(), "Chat is created");
        return new ChatCreationResponse(chat.getId());
    }

    private void createAndSaveNewUserRoleMutedPinnedChat(UUID userId, UUID chatId, Role userRole) {
        userRoleMutedPinnedChatRepository.save(
                UserRoleMutedPinnedChat.create(userId, chatId, userRole)
        );
    }

    private void sendSystemMessage(UUID chatId, String value) {
        messageService.sendMessage(
                SendMessageDto.builder()
                        .chatId(chatId)
                        .dataDtos(List.of(
                                new MessageDataDto(
                                        MessageType.TEXT,
                                        value
                                )
                        ))
                        .dateTime(LocalDateTime.now())
                        .build()
        );
    }

    @Override
    public void updateGroupChat(UpdateGroupChatDto dto) {
        Chat chat = chatRepository.findById(dto.getChatId())
                .orElseThrow(NullPointerException::new);
        List<UserRoleMutedPinnedChat> userRoleMutedPinnedChats =
                userRoleMutedPinnedChatRepository.findAllByChatId(dto.getChatId());
        chat.setTitle(dto.getTitle());
        chat.setIcon(dto.getIcon());
        chatRepository.save(chat);

        removeUsersWithRolesFromGroupChat(dto, userRoleMutedPinnedChats);
        addOrUpdateUsersWithRolesInGroupChat(dto, userRoleMutedPinnedChats);
        userRoleMutedPinnedChatRepository.saveAll(userRoleMutedPinnedChats);
    }

    private void removeUsersWithRolesFromGroupChat(
            UpdateGroupChatDto dto,
            List<UserRoleMutedPinnedChat> userRoleMutedPinnedChats
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
                sendSystemMessage(dto.getChatId(), userRoleMutedPinnedChat.getUserId() + " left the group");
            }
        });
        userRoleMutedPinnedChats.removeIf(userRoleMutedPinnedChat -> !dto.getUsers().containsKey(userRoleMutedPinnedChat.getUserId()));
    }

    private void addOrUpdateUsersWithRolesInGroupChat(
            UpdateGroupChatDto dto,
            List<UserRoleMutedPinnedChat> userRoleMutedPinnedChats
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
                sendSystemMessage(dto.getChatId(), userId + " joined the group");
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
    public void deleteChat(UUID chatId) {
        chatRepository.findById(chatId).orElseThrow(NullPointerException::new);
        chatRepository.deleteById(chatId);
        userRoleMutedPinnedChatRepository.deleteAllByChatId(chatId);
        messageRepository.deleteAllByChatId(chatId);
        userChatCheckedMessageRepository.deleteAllByChatId(chatId);
        Page<ChatMessageDataMessage> page;
        do {
            page = chatMessageDataMessageRepository
                    .findAllByChatId(chatId, PageRequest.of(0, 100))
                    .orElseThrow(NullPointerException::new);
            messageDataRepository.deleteAllByIdIn(
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
    }
}
