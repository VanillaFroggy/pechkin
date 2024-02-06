package ru.intech.pechkin.messenger.infrastructure.service.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.*;
import ru.intech.pechkin.messenger.infrastructure.persistance.repo.*;
import ru.intech.pechkin.messenger.infrastructure.service.ChatService;
import ru.intech.pechkin.messenger.infrastructure.service.dto.*;
import ru.intech.pechkin.messenger.infrastructure.service.mapper.MessengerServiceMapper;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatRepository chatRepository;
    private final UserRoleMutedPinnedChatRepository userRoleMutedPinnedChatRepository;
    private final MessageRepository messageRepository;
    private final MessageDataRepository messageDataRepository;
    private final UserRepository userRepository;
    private final ChatMessageDataMessageRepository chatMessageDataMessageRepository;
    private final UserChatCheckedMessageRepository userChatCheckedMessageRepository;
    private final MessengerServiceMapper mapper;

    @Override
    public List<ChatDto> getAllChats(UUID userId) {
        List<Chat> chats = chatRepository.findAllByIdIn(userRoleMutedPinnedChatRepository.findAllByUserId(userId)
                .stream()
                .map(UserRoleMutedPinnedChat::getChatId)
                .toList());
        if (chats.isEmpty())
            throw new NoSuchElementException("Чаты для данного пользователя пока отстутствуют");
        List<ChatDto> chatDtos = chats.stream()
                .map(mapper::chatToChatDto)
                .toList();
        chatDtos.forEach(chatDto -> {
            Message message = messageRepository.findFirstByChatIdOrderByDateTimeDesc(chatDto.getId());
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

            List<UserRoleMutedPinnedChat> userRoleMutedPinnedChats = userRoleMutedPinnedChatRepository.findAllByChatId(chatDto.getId());
            chatDto.setUsersWithRole(userRoleMutedPinnedChats.stream()
                    .map(userRoleMutedPinnedChat -> userRepository.findById(userRoleMutedPinnedChat.getUserId())
                            .orElseThrow(NullPointerException::new))
                    .map(user -> mapper.userAndRoleToUserWithRoleDto(
                            user,
                            userRoleMutedPinnedChats.stream()
                                    .filter(userRoleMutedPinnedChat -> userRoleMutedPinnedChat.getUserId().equals(user.getId()))
                                    .findFirst()
                                    .orElseThrow(NullPointerException::new)
                                    .getUserRole()))
                    .toList());

            if (chatDto.getChatType().equals(ChatType.P2P)) {
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

            List<UserChatCheckedMessage> userChatCheckedMessages = userChatCheckedMessageRepository
                    .findAllByUserIdAndChatIdAndChecked(userId, chatDto.getId(), false);
            chatDto.setUnreadMessagesCount(
                    userChatCheckedMessages.stream()
                            .filter(userChatCheckedMessage -> !userChatCheckedMessage.getUserId()
                                    .equals(messageRepository.findById(userChatCheckedMessage.getMessageId())
                                            .orElseThrow(NullPointerException::new)
                                            .getPublisher()))
                            .count()
            );

            UserRoleMutedPinnedChat userRoleMutedPinnedChat = userRoleMutedPinnedChatRepository.findByUserIdAndChatId(userId, chatDto.getId());
            chatDto.setMuted(userRoleMutedPinnedChat.getMuted());
            chatDto.setPinned(userRoleMutedPinnedChat.getPinned());
        });
        chatDtos = chatDtos.stream()
                .sorted(
                        Comparator.comparing(ChatDto::getPinned)
                                .thenComparing(chatDto -> chatDto.getMessage().getDateTime())
                                .reversed()
                )
                .toList();
        return chatDtos;
    }

    @Override
    public ChatCreationResponse createFavoritesChat(UUID userId) {
        Chat chat = Chat.createFavorites();
        chatRepository.save(chat);
        userRoleMutedPinnedChatRepository.save(
                UserRoleMutedPinnedChat.builder()
                        .id(UUID.randomUUID())
                        .chatId(chat.getId())
                        .userId(userId)
                        .userRole(Role.ADMIN)
                        .muted(false)
                        .pinned(false)
                        .build()
        );
        UUID messageId = createFirstMessageForFavoritesOrGroupChat(chat.getId());
        userChatCheckedMessageRepository.save(
                UserChatCheckedMessage.builder()
                        .id(UUID.randomUUID())
                        .userId(userId)
                        .chatId(chat.getId())
                        .messageId(messageId)
                        .checked(false)
                        .build()
        );
        return new ChatCreationResponse(chat.getId());
    }

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
        dto.getUsers().forEach(userId -> userRoleMutedPinnedChatRepository.save(
                UserRoleMutedPinnedChat.builder()
                        .id(UUID.randomUUID())
                        .chatId(chat.getId())
                        .userId(userId)
                        .userRole(Role.ADMIN)
                        .muted(false)
                        .pinned(false)
                        .build()
        ));
        List<MessageData> datas = dto.getMessageDto()
                .getDataDtos()
                .stream()
                .map(createP2PChatMessageDataDto -> mapper.messageDataDtoToEntity(
                        UUID.randomUUID(),
                        createP2PChatMessageDataDto)
                )
                .toList();
        Message message = Message.builder()
                .id(UUID.randomUUID())
                .chatId(chat.getId())
                .publisher(dto.getMessageDto().getPublisher())
                .datas(datas)
                .dateTime(dto.getMessageDto().getDateTime())
                .edited(false)
                .build();
        datas.forEach(messageData -> chatMessageDataMessageRepository.save(
                ChatMessageDataMessage.builder()
                        .id(UUID.randomUUID())
                        .chatId(chat.getId())
                        .messageId(message.getId())
                        .messageDataId(messageData.getId())
                        .build()
        ));
        messageDataRepository.saveAll(datas);
        messageRepository.save(message);
        dto.getUsers().forEach(uuid -> userChatCheckedMessageRepository.save(
                UserChatCheckedMessage.builder()
                        .id(UUID.randomUUID())
                        .userId(uuid)
                        .chatId(chat.getId())
                        .messageId(message.getId())
                        .checked(false)
                        .build()
        ));
        return new ChatCreationResponse(chat.getId());
    }

    @Override
    public ChatCreationResponse createGroupChat(CreateGroupChatDto dto) {
        if (dto.getUsers().isEmpty() ||
                userRepository.findByIdIn(dto.getUsers().keySet().stream().toList())
                        .orElseThrow(NullPointerException::new).isEmpty()) {
            throw new IllegalArgumentException("В групповом чате должны быть пользователи");
        }
        Chat chat = Chat.createGroup(dto.getTitle(), dto.getIcon());
        dto.getUsers().forEach((key, value) -> userRoleMutedPinnedChatRepository.save(
                UserRoleMutedPinnedChat.builder()
                        .id(UUID.randomUUID())
                        .chatId(chat.getId())
                        .userId(key)
                        .userRole(value)
                        .muted(false)
                        .pinned(false)
                        .build()
        ));
        chatRepository.save(chat);
        UUID messageId = createFirstMessageForFavoritesOrGroupChat(chat.getId());
        dto.getUsers().forEach((key, value) -> userChatCheckedMessageRepository.save(
                UserChatCheckedMessage.builder()
                        .id(UUID.randomUUID())
                        .userId(key)
                        .chatId(chat.getId())
                        .messageId(messageId)
                        .checked(false)
                        .build()
        ));
        return new ChatCreationResponse(chat.getId());
    }


    @Override
    public void updateGroupChat(UpdateGroupChatDto dto) {
        Chat chat = chatRepository.findById(dto.getChatId())
                .orElseThrow(NullPointerException::new);
        List<UserRoleMutedPinnedChat> userRoleMutedPinnedChats = userRoleMutedPinnedChatRepository.findAllByChatId(dto.getChatId());
        chat.setTitle(dto.getTitle());
        chat.setIcon(dto.getIcon());
        chatRepository.save(chat);
        userRoleMutedPinnedChats.forEach(userRoleMutedPinnedChat -> {
            Map.Entry<UUID, Role> filteredDto = dto.getUsers().entrySet().stream()
                    .filter(entry -> entry.getKey().equals(userRoleMutedPinnedChat.getUserId()))
                    .findFirst()
                    .orElse(null);
            if (filteredDto == null)
                userRoleMutedPinnedChatRepository.deleteByUserId(userRoleMutedPinnedChat.getUserId());
        });
        userRoleMutedPinnedChats.removeIf(userRoleMutedPinnedChat -> !dto.getUsers().containsKey(userRoleMutedPinnedChat.getUserId()));
        dto.getUsers().forEach((key, value) -> {
            UserRoleMutedPinnedChat filteredUserRoleMutedPinnedChat = userRoleMutedPinnedChats.stream()
                    .filter(userRoleMutedPinnedChat -> key.equals(userRoleMutedPinnedChat.getUserId()))
                    .findFirst()
                    .orElse(null);
            if (filteredUserRoleMutedPinnedChat == null) {
                userRoleMutedPinnedChats.add(
                        UserRoleMutedPinnedChat.builder()
                                .id(UUID.randomUUID())
                                .chatId(dto.getChatId())
                                .userId(key)
                                .userRole(value)
                                .build()
                );
            } else {
                userRoleMutedPinnedChats.set(
                        userRoleMutedPinnedChats.indexOf(filteredUserRoleMutedPinnedChat),
                        UserRoleMutedPinnedChat.builder()
                                .id(filteredUserRoleMutedPinnedChat.getId())
                                .chatId(filteredUserRoleMutedPinnedChat.getChatId())
                                .userId(key)
                                .userRole(value)
                                .build()
                );
            }
        });
        userRoleMutedPinnedChatRepository.saveAll(userRoleMutedPinnedChats);
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

    private UUID createFirstMessageForFavoritesOrGroupChat(UUID chatId) {
        MessageData messageData = new MessageData(
                UUID.randomUUID(),
                MessageType.TEXT,
                "Chat is created"
        );
        Message message = Message.builder()
                .id(UUID.randomUUID())
                .chatId(chatId)
                .datas(List.of(messageData))
                .dateTime(LocalDateTime.now())
                .build();
        chatMessageDataMessageRepository.save(
                ChatMessageDataMessage.builder()
                        .id(UUID.randomUUID())
                        .chatId(chatId)
                        .messageId(message.getId())
                        .messageDataId(messageData.getId())
                        .build()
        );
        messageDataRepository.save(messageData);
        messageRepository.save(message);
        return message.getId();
    }
}
