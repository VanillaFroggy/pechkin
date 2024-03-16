package ru.intech.pechkin.messenger.infrastructure.service;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import ru.intech.pechkin.messenger.infrastructure.service.dto.chat.*;
import ru.intech.pechkin.messenger.infrastructure.service.dto.user.UserWithRoleDto;

import java.util.List;
import java.util.UUID;

public interface ChatService {
    Page<ChatDto> getPageOfChats(@Valid GetPageOfChatsDto dto);

    ChatDto getChatByIdAndUserId(GetChatByIdAndUserIdDto dto);

    ChatDto getP2PChatByUsers(GetP2PChatByUsersDto dto);

    ChatDto createFavoritesChat(UUID userId);

    ChatDto createP2PChat(@Valid CreateP2PChatDto dto);

    ChatDto createGroupChat(CreateGroupChatDto dto);

    ChatDto updateGroupChat(UpdateGroupChatDto dto);

    void updateGroupChatTitle(UpdateGroupChatTitleOrIconDto dto);

    void updateGroupChatIcon(UpdateGroupChatTitleOrIconDto dto);

    UserWithRoleDto addUserToGroupChat(UpdateUserInGroupChatDto dto);

    UserWithRoleDto updateUserInGroupChat(UpdateUserInGroupChatDto dto);

    UserWithRoleDto removeUserFromGroupChat(UpdateUserInGroupChatDto dto);

    void updateChatMutedStatus(UpdateChatMutedOrPinnedStatusDto dto);

    void updateChatPinnedStatus(UpdateChatMutedOrPinnedStatusDto dto);

    List<UUID> deleteChat(UUID chatId);
}
