package ru.intech.pechkin.messenger.infrastructure.persistance.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.UserRoleChat;

import java.util.List;
import java.util.UUID;

public interface UserRoleChatRepository extends MongoRepository<UserRoleChat, UUID> {
    List<UserRoleChat> findAllByUserId(UUID userId);

    List<UserRoleChat> findAllByChatId(UUID chatId);

    void deleteAllByChatId(UUID chatId);
}
