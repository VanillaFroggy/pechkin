package ru.intech.pechkin.messenger.infrastructure.persistence.repo;

import org.springframework.stereotype.Repository;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.ChatType;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.UserRoleMutedPinnedChat;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRoleMutedPinnedChatRepositoryCustom {
    List<UserRoleMutedPinnedChat> findByUserIdInAndChatType(List<UUID> userIds, ChatType chatType);
}
