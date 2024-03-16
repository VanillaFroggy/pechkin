package ru.intech.pechkin.messenger.infrastructure.persistence.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.UserChatCheckedMessage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserChatCheckedMessageRepository extends MongoRepository<UserChatCheckedMessage, UUID> {
    Optional<UserChatCheckedMessage> findByUserIdAndChatIdAndMessageId(UUID userId, UUID chatId, UUID messageId);

    Optional<List<UserChatCheckedMessage>> findAllByUserIdAndChatIdAndMessageIdIn(
            UUID userId,
            UUID chatId,
            List<UUID> messageIds
    );

    List<UserChatCheckedMessage> findAllByUserIdAndChatIdInAndMessageIdIn(
            UUID userId,
            List<UUID> chatIds,
            List<UUID> messageIds
    );

    Page<UserChatCheckedMessage> findAllByUserIdAndChatIdAndChecked(
            UUID userId,
            UUID chatId,
            Boolean checked,
            Pageable pageable
    );

    List<UserChatCheckedMessage> findAllByUserIdInAndChatIdAndMessageIdAndChecked(
            List<UUID> userIds,
            UUID chatId,
            UUID messageId,
            Boolean checked
    );

    List<UserChatCheckedMessage> findAllByUserIdInAndChatIdAndMessageIdInAndChecked(
            List<UUID> userIds,
            UUID chatId,
            Collection<UUID> messageIds,
            Boolean checked
    );

    void deleteAllByChatId(UUID chatId);

    void deleteAllByMessageIdAndChatId(UUID messageId, UUID chatId);

    void deleteAllByMessageIdInAndChatId(Collection<UUID> messageIds, UUID chatId);
}
