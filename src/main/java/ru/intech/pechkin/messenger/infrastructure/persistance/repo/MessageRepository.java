package ru.intech.pechkin.messenger.infrastructure.persistance.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository extends MongoRepository<Message, UUID> {
    Page<Message> findAllByChatIdOrderByDateTimeDesc(UUID chatId, Pageable pageable);

    Message findFirstByChatIdOrderByDateTimeDesc(UUID chatId);

    Optional<Page<Message>> findAllByChatIdAndIdIn(UUID chatId, List<UUID> ids, Pageable pageable);

    Optional<Message> findAllByChatId(UUID chatId);

    void deleteAllByChatId(UUID chatId);
}
