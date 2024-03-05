package ru.intech.pechkin.messenger.infrastructure.persistence.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.UserChatCheckedMessage;

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

    List<UserChatCheckedMessage> findAllByUserIdAndChatIdAndChecked(UUID userId, UUID chatId, Boolean checked);

    List<UserChatCheckedMessage> findAllByUserIdInAndChatIdAndMessageIdAndChecked(
            List<UUID> userIds,
            UUID chatId,
            UUID messageId,
            Boolean checked
    );

    void deleteAllByChatId(UUID chatId);

    void deleteAllByMessageIdAndChatId(UUID messageId, UUID chatId);
}
