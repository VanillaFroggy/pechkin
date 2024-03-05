package ru.intech.pechkin.messenger.infrastructure.persistence.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.ChatMessageDataMessage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatMessageDataMessageRepository extends MongoRepository<ChatMessageDataMessage, UUID> {
    Optional<Page<ChatMessageDataMessage>> findAllByChatId(UUID chatId, Pageable pageable);

    List<ChatMessageDataMessage> findAllByChatIdAndMessageId(UUID chatId, UUID messageId);

    void deleteAllByChatIdAndIdIn(UUID chatId, List<UUID> ids);

    void deleteAllByMessageIdAndChatId(UUID messageId, UUID chatId);
}
