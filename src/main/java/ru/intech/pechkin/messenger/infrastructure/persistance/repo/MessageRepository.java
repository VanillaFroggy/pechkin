package ru.intech.pechkin.messenger.infrastructure.persistance.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.Message;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository extends MongoRepository<Message, UUID> {
    Page<Message> findAllByChatIdOrderByDateTimeDesc(UUID chatId, Pageable pageable);

    Page<Message> findAllByChatIdAndDateTimeAfterOrderByDateTimeDesc(
            UUID chatId, LocalDateTime dateTime, Pageable pageable
    );

    Message findFirstByChatIdOrderByDateTimeDesc(UUID chatId);

    Optional<Message> findByIdAndChatId(UUID id, UUID chatId);

    void deleteAllByChatId(UUID chatId);
}
