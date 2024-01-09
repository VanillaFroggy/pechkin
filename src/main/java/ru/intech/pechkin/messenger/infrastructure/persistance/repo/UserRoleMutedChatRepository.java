package ru.intech.pechkin.messenger.infrastructure.persistance.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.UserRoleMutedChat;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRoleMutedChatRepository extends MongoRepository<UserRoleMutedChat, UUID> {
    List<UserRoleMutedChat> findAllByUserId(UUID userId);

    List<UserRoleMutedChat> findAllByChatId(UUID chatId);

    UserRoleMutedChat findByUserIdAndChatId(UUID userId, UUID chatId);

    void deleteAllByChatId(UUID chatId);

    void deleteByUserId(UUID userId);
}
