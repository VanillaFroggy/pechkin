package ru.intech.pechkin.messenger.infrastructure.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import ru.intech.pechkin.messenger.infrastructure.service.dto.chat.*;
import ru.intech.pechkin.messenger.infrastructure.service.dto.user.UserWithRoleDto;

import java.util.List;
import java.util.UUID;

@Validated
public interface ChatService {
    Page<ChatDto> getPageOfChats(@Valid GetPageOfChatsDto dto);

    ChatDto getChatByIdAndUserId(@Valid GetChatByIdAndUserIdDto dto);

    ChatDto getP2PChatByUsers(@Valid GetP2PChatByUsersDto dto);

    ChatDto createFavoritesChat(@NotNull UUID userId);

    ChatDto createP2PChat(@Valid CreateP2PChatDto dto);

    ChatDto createGroupChat(@Valid CreateGroupChatDto dto);

    ChatDto updateGroupChat(@Valid UpdateGroupChatDto dto);

    void updateGroupChatTitle(@Valid UpdateGroupChatTitleOrIconDto dto);

    void updateGroupChatIcon(@Valid UpdateGroupChatTitleOrIconDto dto);

    UserWithRoleDto addUserToGroupChat(@Valid UpdateUserInGroupChatDto dto);

    UserWithRoleDto updateUserInGroupChat(@Valid UpdateUserInGroupChatDto dto);

    UserWithRoleDto removeUserFromGroupChat(@Valid UpdateUserInGroupChatDto dto);

    void updateChatMutedStatus(@Valid UpdateChatMutedOrPinnedStatusDto dto);

    void updateChatPinnedStatus(@Valid UpdateChatMutedOrPinnedStatusDto dto);

    List<UUID> deleteChat(@NotNull UUID chatId);
}
