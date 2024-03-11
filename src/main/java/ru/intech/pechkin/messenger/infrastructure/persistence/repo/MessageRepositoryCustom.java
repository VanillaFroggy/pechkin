package ru.intech.pechkin.messenger.infrastructure.persistence.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.Message;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.UserRoleMutedPinnedChat;

import java.util.List;

@Repository
public interface MessageRepositoryCustom {
    Page<Message> findLatestMessagesByChatIdIn(
            List<UserRoleMutedPinnedChat> userRoleMutedPinnedChats,
            Pageable pageable
    );
}
