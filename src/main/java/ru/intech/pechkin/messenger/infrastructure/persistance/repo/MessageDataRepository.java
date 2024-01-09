package ru.intech.pechkin.messenger.infrastructure.persistance.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.MessageData;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageDataRepository extends MongoRepository<MessageData, UUID> {
    Optional<Page<MessageData>> findAllByIdInAndValueLikeIgnoreCase(List<UUID> ids, String value, Pageable pageable);

    void deleteAllByIdIn(List<UUID> ids);
}
