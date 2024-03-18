package ru.intech.pechkin.messenger.infrastructure.persistence.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.MessageData;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageDataRepository extends MongoRepository<MessageData, UUID> {
    @Query(value = "{ 'id' : { $in : ?0 }, 'value' : { $regex: ?1, $options: 'i' } }", count = true)
    Optional<Page<MessageData>> findAllByIdInAndValueLikeIgnoreCase(Collection<UUID> ids, String value, Pageable pageable);
}
