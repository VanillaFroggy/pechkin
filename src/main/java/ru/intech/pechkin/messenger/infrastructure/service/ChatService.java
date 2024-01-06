package ru.intech.pechkin.messenger.infrastructure.service;

import ru.intech.pechkin.messenger.infrastructure.service.dto.ChatDto;
import ru.intech.pechkin.messenger.infrastructure.service.dto.CreateGroupChatDto;
import ru.intech.pechkin.messenger.infrastructure.service.dto.CreateP2PChatDto;
import ru.intech.pechkin.messenger.infrastructure.service.dto.UpdateGroupChatDto;

import java.util.List;
import java.util.UUID;

public interface ChatService {
    List<ChatDto> getAllChats(UUID userId);

    void createFavoritesChat(UUID userId);

    void createP2PChat(CreateP2PChatDto dto);

    void createGroupChat(CreateGroupChatDto dto);

    void updateGroupChat(UpdateGroupChatDto dto);

    void deleteChat(UUID chatId);
}
