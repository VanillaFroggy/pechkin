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
    private final UserRoleMutedChatRepository userRoleMutedChatRepository;
    private final MessageRepository messageRepository;
    private final MessageDataRepository messageDataRepository;
    private final UserRepository userRepository;
    private final ChatMessageDataMessageRepository chatMessageDataMessageRepository;
    private final UserChatCheckedMessageRepository userChatCheckedMessageRepository;
    private final MessengerServiceMapper mapper;

    @Override
    public List<ChatDto> getAllChats(UUID userId) {
        List<Chat> chats = chatRepository.findAllByIdIn(userRoleMutedChatRepository.findAllByUserId(userId)
                .stream()
                .map(UserRoleMutedChat::getChatId)
                .toList());
        if (chats.isEmpty())
            throw new NoSuchElementException("Чаты для данного пользователя пока отстутствуют");
        List<ChatDto> chatDtos = chats.stream()
                .map(mapper::chatToChatDto)
                .toList();
        chatDtos.forEach(chatDto -> {
            Message message = messageRepository.findFirstByChatIdOrderByDateTimeDesc(chatDto.getId());
            boolean checked = userChatCheckedMessageRepository.findByUserIdAndChatIdAndMessageId(
                    userId, chatDto.getId(), message.getId()
            ).orElseThrow(NullPointerException::new).getChecked();
            chatDto.setMessage(mapper.messageToMessageDto(message, checked));

            List<UserRoleMutedChat> userRoleMutedChats = userRoleMutedChatRepository.findAllByChatId(chatDto.getId());
            chatDto.setUsersWithRole(userRoleMutedChats.stream()
                    .map(userRoleMutedChat -> userRepository.findById(userRoleMutedChat.getUserId())
                            .orElseThrow(NullPointerException::new))
                    .map(user -> mapper.userAndRoleToUserWithRoleDto(
                            user,
                            userRoleMutedChats.stream()
                                    .filter(userRoleMutedChat -> userRoleMutedChat.getUserId().equals(user.getId()))
                                    .findFirst()
                                    .orElseThrow(NullPointerException::new)
                                    .getUserRole()))
                    .toList());

            if (chatDto.getChatType().equals(ChatType.P2P)) {
                User otherUser = userRepository.findById(
                        userRoleMutedChats.stream()
                                .filter(userRoleMutedChat -> !userRoleMutedChat.getUserId().equals(userId))
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

            chatDto.setMuted(userRoleMutedChatRepository.findByUserIdAndChatId(userId, chatDto.getId()).getMuted());
        });
        chatDtos = chatDtos.stream()
                .sorted(Comparator.comparing(chatDto -> chatDto.getMessage().getDateTime()))
                .toList();
        List<ChatDto> result = new ArrayList<>();
        for (int i = chatDtos.size() - 1; i >= 0; i--)
            result.add(chatDtos.get(i));
        return result;
    }

    @Override
    public ChatCreationResponse createFavoritesChat(UUID userId) {
        Chat chat = Chat.createFavorites();
        chatRepository.save(chat);
        userRoleMutedChatRepository.save(
                UserRoleMutedChat.builder()
                        .id(UUID.randomUUID())
                        .chatId(chat.getId())
                        .userId(userId)
                        .userRole(Role.ADMIN)
                        .muted(false)
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
        dto.getUsers().forEach(userId -> userRoleMutedChatRepository.save(
                UserRoleMutedChat.builder()
                        .id(UUID.randomUUID())
                        .chatId(chat.getId())
                        .userId(userId)
                        .userRole(Role.ADMIN)
                        .muted(false)
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
        dto.getUsers().forEach((key, value) -> userRoleMutedChatRepository.save(
                UserRoleMutedChat.builder()
                        .id(UUID.randomUUID())
                        .chatId(chat.getId())
                        .userId(key)
                        .userRole(value)
                        .muted(false)
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
        List<UserRoleMutedChat> userRoleMutedChats = userRoleMutedChatRepository.findAllByChatId(dto.getChatId());
        chat.setTitle(dto.getTitle());
        chat.setIcon(dto.getIcon());
        chatRepository.save(chat);
        userRoleMutedChats.forEach(userRoleMutedChat -> {
            Map.Entry<UUID, Role> filteredDto = dto.getUsers().entrySet().stream()
                    .filter(entry -> entry.getKey().equals(userRoleMutedChat.getUserId()))
                    .findFirst()
                    .orElse(null);
            if (filteredDto == null)
                userRoleMutedChatRepository.deleteByUserId(userRoleMutedChat.getUserId());
        });
        userRoleMutedChats.removeIf(userRoleMutedChat -> !dto.getUsers().containsKey(userRoleMutedChat.getUserId()));
        dto.getUsers().forEach((key, value) -> {
            UserRoleMutedChat filteredUserRoleMutedChat = userRoleMutedChats.stream()
                    .filter(userRoleMutedChat -> key.equals(userRoleMutedChat.getUserId()))
                    .findFirst()
                    .orElse(null);
            if (filteredUserRoleMutedChat == null) {
                userRoleMutedChats.add(
                        UserRoleMutedChat.builder()
                                .id(UUID.randomUUID())
                                .chatId(dto.getChatId())
                                .userId(key)
                                .userRole(value)
                                .build()
                );
            } else {
                userRoleMutedChats.set(
                        userRoleMutedChats.indexOf(filteredUserRoleMutedChat),
                        UserRoleMutedChat.builder()
                                .id(filteredUserRoleMutedChat.getId())
                                .chatId(filteredUserRoleMutedChat.getChatId())
                                .userId(key)
                                .userRole(value)
                                .build()
                );
            }
        });
        userRoleMutedChatRepository.saveAll(userRoleMutedChats);
    }

    @Override
    public void updateChatMutedStatus(UpdateChatMutedStatusDto dto) {
        UserRoleMutedChat userRoleMutedChat =
                userRoleMutedChatRepository.findByUserIdAndChatId(dto.getUserId(), dto.getChatId());
        userRoleMutedChat.setMuted(dto.getMuted());
        userRoleMutedChatRepository.save(userRoleMutedChat);
    }

    @Override
    public void deleteChat(UUID chatId) {
        chatRepository.findById(chatId).orElseThrow(NullPointerException::new);
        chatRepository.deleteById(chatId);
        userRoleMutedChatRepository.deleteAllByChatId(chatId);
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
