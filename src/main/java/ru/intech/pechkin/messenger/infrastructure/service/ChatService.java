package ru.intech.pechkin.messenger.infrastructure.service;

import jakarta.validation.Valid;
import ru.intech.pechkin.messenger.infrastructure.service.dto.*;

import java.util.List;
import java.util.UUID;

public interface ChatService {
    List<ChatDto> getAllChats(UUID userId);

    ChatDto createFavoritesChat(UUID userId);

    ChatDto createP2PChat(@Valid CreateP2PChatDto dto);

    ChatDto createGroupChat(CreateGroupChatDto dto);

    ChatDto updateGroupChat(UpdateGroupChatDto dto);

    void updateChatMutedStatus(UpdateChatMutedOrPinnedStatusDto dto);

    void updateChatPinnedStatus(UpdateChatMutedOrPinnedStatusDto dto);

    List<UUID> deleteChat(UUID chatId);
}
