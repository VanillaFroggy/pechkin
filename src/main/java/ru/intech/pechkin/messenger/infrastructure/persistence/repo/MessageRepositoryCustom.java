package ru.intech.pechkin.messenger.infrastructure.persistence.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.Message;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.UserRoleMutedPinnedChat;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepositoryCustom {
    Page<Message> findLatestMessagesByChatIdIn(
            List<UserRoleMutedPinnedChat> userRoleMutedPinnedChats,
            Pageable pageable
    );

    Page<Message> findAllByUserIdChatIdAndPublisherNotAndChecked(
            UUID userId,
            UUID chatId,
            UUID publisher,
            Boolean checked,
            Pageable pageable
    );
}
