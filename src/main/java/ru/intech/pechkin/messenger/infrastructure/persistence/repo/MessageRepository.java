package ru.intech.pechkin.messenger.infrastructure.persistence.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.Message;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository extends MongoRepository<Message, UUID>, MessageRepositoryCustom {
    Page<Message> findAllByChatIdOrderByDateTimeDesc(UUID chatId, Pageable pageable);

    Page<Message> findAllByChatIdAndDateTimeAfterOrderByDateTime(
            UUID chatId, ZonedDateTime dateTime, Pageable pageable
    );

    Page<Message> findAllByChatIdAndDateTimeBeforeOrderByDateTime(
            UUID chatId, ZonedDateTime dateTime, Pageable pageable
    );

    Message findFirstByChatIdAndIdInOrderByDateTimeDesc(UUID chatId, Collection<UUID> id);

    Optional<Message> findFirstByChatIdOrderByDateTimeDesc(UUID chatId);

    List<Message> findAllByChatIdAndIdIn(UUID chatId, List<UUID> ids);

    List<Message> findAllByChatIdAndPublisherNot(UUID chatId, UUID publisher);

    Optional<Message> findByIdAndChatId(UUID id, UUID chatId);

    void deleteAllByChatId(UUID chatId);
}
