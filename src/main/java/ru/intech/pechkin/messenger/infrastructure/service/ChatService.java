package ru.intech.pechkin.messenger.infrastructure.service;

import jakarta.validation.Valid;
import ru.intech.pechkin.messenger.infrastructure.service.dto.*;

import java.util.List;
import java.util.UUID;

public interface ChatService {
    List<ChatDto> getAllChats(UUID userId);

    ChatCreationResponse createFavoritesChat(UUID userId);

    ChatCreationResponse createP2PChat(@Valid CreateP2PChatDto dto);

    ChatCreationResponse createGroupChat(CreateGroupChatDto dto);

    void updateGroupChat(UpdateGroupChatDto dto);

    void updateChatMutedStatus(UpdateChatMutedStatusDto dto);

    void deleteChat(UUID chatId);
}
