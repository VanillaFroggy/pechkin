package ru.intech.pechkin.messenger.infrastructure.persistence.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.Chat;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRepository extends MongoRepository<Chat, UUID> {
    Optional<Chat> findByDepartmentId(UUID departmentId);
}
