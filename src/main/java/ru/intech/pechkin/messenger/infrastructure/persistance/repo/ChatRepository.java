package ru.intech.pechkin.messenger.infrastructure.persistance.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.Chat;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatRepository extends MongoRepository<Chat, UUID> {
    List<Chat> findAllByIdIn(List<UUID> uuids);
}
