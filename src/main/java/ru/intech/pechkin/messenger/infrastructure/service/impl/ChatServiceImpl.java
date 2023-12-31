package ru.intech.pechkin.messenger.infrastructure.service.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.*;
import ru.intech.pechkin.messenger.infrastructure.persistance.repo.*;
import ru.intech.pechkin.messenger.infrastructure.service.ChatService;
import ru.intech.pechkin.messenger.infrastructure.service.dto.ChatDto;
import ru.intech.pechkin.messenger.infrastructure.service.dto.CreateGroupChatDto;
import ru.intech.pechkin.messenger.infrastructure.service.dto.CreateP2PChatDto;
import ru.intech.pechkin.messenger.infrastructure.service.dto.UpdateGroupChatDto;
import ru.intech.pechkin.messenger.infrastructure.service.mapper.ChatServiceMapper;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatRepository chatRepository;
    private final UserRoleChatRepository userRoleChatRepository;
    private final MessageRepository messageRepository;
    private final MessageDataRepository messageDataRepository;
    private final UserRepository userRepository;
    private final ChatMessageDataMessageRepository chatMessageDataMessageRepository;
    private final ChatServiceMapper mapper;

    @Override
    public List<ChatDto> getAllChats(UUID userId) {
        List<Chat> chats = chatRepository.findAllByIdIn(userRoleChatRepository.findAllByUserId(userId)
                .stream()
                .map(UserRoleChat::getChatId)
                .toList());
        if (chats.isEmpty())
            throw new NoSuchElementException("Чаты для данного пользователя пока отстутствуют");
        List<ChatDto> chatDtos = chats.stream()
                .map(mapper::chatToChatDto)
                .toList();
        chatDtos.forEach(chatDto ->
                chatDto.setMessage(messageRepository.findFirstByChatIdOrderByDateTimeDesc(chatDto.getId())));
        chatDtos.forEach(chatDto -> {
            List<UserRoleChat> userRoleChats = userRoleChatRepository.findAllByChatId(chatDto.getId());
            chatDto.setUsersWithRole(userRoleChats.stream()
                    .map(userRoleChat -> userRepository.findById(userRoleChat.getUserId())
                            .orElseThrow(NullPointerException::new))
                    .map(user -> mapper.userAndRoleToUserWithRoleDto(
                            user,
                            userRoleChats.stream()
                                    .filter(userRoleChat -> userRoleChat.getUserId().equals(user.getId()))
                                    .findFirst()
                                    .orElseThrow(NullPointerException::new)
                                    .getUserRole()))
                    .toList());
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
    public void createFavoritesChat(UUID userId) {
        Chat chat = Chat.createFavorites();
        chatRepository.save(chat);
        userRoleChatRepository.save(
                UserRoleChat.builder()
                        .id(UUID.randomUUID())
                        .chatId(chat.getId())
                        .userId(userId)
                        .userRole(Role.ADMIN)
                        .build()
        );
        createFirstMessageForFavoritesOrGroupChat(chat.getId());
    }

    @Override
    public void createP2PChat(@Valid CreateP2PChatDto dto) {
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
        dto.getUsers().forEach(userId -> userRoleChatRepository.save(
                UserRoleChat.builder()
                        .id(UUID.randomUUID())
                        .chatId(chat.getId())
                        .userId(userId)
                        .userRole(Role.ADMIN)
                        .build()
        ));
        List<MessageData> datas = dto.getMessageDto()
                .getDataDtos()
                .stream()
                .map(createP2PChatMessageDataDto -> mapper.MessageDataDtoToEntity(
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
                .checked(false)
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
    }

    @Override
    public void createGroupChat(CreateGroupChatDto dto) {
        if (dto.getUsers().isEmpty() ||
                userRepository.findByIdIn(dto.getUsers().keySet().stream().toList())
                        .orElseThrow(NullPointerException::new).isEmpty()) {
            throw new IllegalArgumentException("В групповом чате должно быть больше двух пользователей");
        }
        Chat chat = Chat.createGroup(dto.getTitle());
        dto.getUsers().forEach((key, value) -> userRoleChatRepository.save(
                UserRoleChat.builder()
                        .id(UUID.randomUUID())
                        .chatId(chat.getId())
                        .userId(key)
                        .userRole(value)
                        .build()
        ));
        chatRepository.save(chat);
        createFirstMessageForFavoritesOrGroupChat(chat.getId());
    }


    @Override
    public void updateGroupChat(UpdateGroupChatDto dto) {
        Chat chat = chatRepository.findById(dto.getChatId())
                .orElseThrow(NullPointerException::new);
        List<UserRoleChat> userRoleChats = userRoleChatRepository.findAllByChatId(dto.getChatId());
        chat.setTitle(dto.getTitle());
        chatRepository.save(chat);
        userRoleChats.forEach(userRoleChat -> {
            Map.Entry<UUID, Role> filteredDto = dto.getUsers().entrySet().stream()
                    .filter(entry -> entry.getKey().equals(userRoleChat.getUserId()))
                    .findFirst()
                    .orElse(null);
            if (filteredDto == null)
                userRoleChatRepository.deleteByUserId(userRoleChat.getUserId());
        });
        userRoleChats.removeIf(userRoleChat -> !dto.getUsers().containsKey(userRoleChat.getUserId()));
        dto.getUsers().forEach((key, value) -> {
            UserRoleChat filteredUserRoleChat = userRoleChats.stream()
                    .filter(userRoleChat -> key.equals(userRoleChat.getUserId()))
                    .findFirst()
                    .orElse(null);
            if (filteredUserRoleChat == null) {
                userRoleChats.add(
                        UserRoleChat.builder()
                                .id(UUID.randomUUID())
                                .chatId(dto.getChatId())
                                .userId(key)
                                .userRole(value)
                                .build()
                );
            } else {
                userRoleChats.set(
                        userRoleChats.indexOf(filteredUserRoleChat),
                        UserRoleChat.builder()
                                .id(filteredUserRoleChat.getId())
                                .chatId(filteredUserRoleChat.getChatId())
                                .userId(key)
                                .userRole(value)
                                .build()
                );
            }
        });
        userRoleChatRepository.saveAll(userRoleChats);
    }

    @Override
    public void deleteChat(UUID chatId) {
        chatRepository.deleteById(chatId);
        userRoleChatRepository.deleteAllByChatId(chatId);
        messageRepository.deleteAllByChatId(chatId);
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

    private void createFirstMessageForFavoritesOrGroupChat(UUID chatId) {
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
                .checked(false)
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
    }

}
