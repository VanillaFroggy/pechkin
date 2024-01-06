package ru.intech.pechkin.messenger.infrastructure.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.Chat;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.ChatMessageDataMessage;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.Role;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.UserRoleChat;
import ru.intech.pechkin.messenger.infrastructure.persistance.repo.*;
import ru.intech.pechkin.messenger.infrastructure.service.ChatService;
import ru.intech.pechkin.messenger.infrastructure.service.dto.*;
import ru.intech.pechkin.messenger.infrastructure.service.mapper.ChatServiceMapper;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

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
        List<UserRoleChat> userRoleChats = userRoleChatRepository.findAllByUserId(userId);
        List<Chat> chats = chatRepository.findAllByIdIn(userRoleChats.stream()
                .map(UserRoleChat::getChatId)
                .toList());
        if (chats.isEmpty())
            throw new NoSuchElementException("Чаты для данного пользователя пока отстутствуют");
        List<ChatDto> chatDtos = chats.stream()
                .map(mapper::chatToChatDto)
                .toList();
        chatDtos.forEach(chatDto ->
                chatDto.setMessage(messageRepository.findFirstByChatIdOrderByDateTimeDesc(chatDto.getId())));

        chatDtos.forEach(chatDto -> chatDto.setUsersWithRole(userRoleChats.stream()
                .filter(userRoleChat -> userRoleChat.getChatId().equals(chatDto.getId()))
                .map(userRoleChat -> userRepository.findById(userRoleChat.getUserId())
                        .orElseThrow(NullPointerException::new))
                .map(user -> mapper.userAndRoleToUserWithRoleDto(
                        user,
                        userRoleChats.stream()
                                .filter(userRoleChat -> userRoleChat.getUserId().equals(user.getId()))
                                .findFirst()
                                .orElseThrow(NullPointerException::new)
                                .getUserRole()))
                .toList()));
        return chatDtos;
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
    }

    @Override
    public void createP2PChat(CreateP2PChatDto dto) {
        if (dto.getUsers().size() != 2 ||
                userRepository.findByIdIn(dto.getUsers())
                        .orElseThrow(NullPointerException::new)
                        .size() != 2) {
            throw new IllegalArgumentException("В приватном чате должно быть лишь два пользователя");
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
    }

    @Override
    public void createGroupChat(CreateGroupChatDto dto) {
        if (dto.getUsers().size() <= 2 ||
                userRepository.findByIdIn(dto.getUsers().keySet().stream().toList())
                        .orElseThrow(NullPointerException::new)
                        .size() <= 2) {
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
    }

    @Override
    public void updateGroupChat(UpdateGroupChatDto dto) {
        Chat chat = chatRepository.findById(dto.getChatId())
                .orElseThrow(NullPointerException::new);
        List<UserRoleChat> userRoleChats = userRoleChatRepository.findAllByChatId(dto.getChatId());
        chat.setTitle(dto.getTitle());
        chatRepository.save(chat);
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
}
