package ru.intech.pechkin.messenger.infrastructure.persistance.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.UserRoleMutedPinnedChat;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRoleMutedPinnedChatRepository extends MongoRepository<UserRoleMutedPinnedChat, UUID> {
    List<UserRoleMutedPinnedChat> findAllByUserId(UUID userId);

    List<UserRoleMutedPinnedChat> findAllByChatId(UUID chatId);

    UserRoleMutedPinnedChat findByUserIdAndChatId(UUID userId, UUID chatId);

    void deleteAllByChatId(UUID chatId);

    void deleteByUserId(UUID userId);
}
