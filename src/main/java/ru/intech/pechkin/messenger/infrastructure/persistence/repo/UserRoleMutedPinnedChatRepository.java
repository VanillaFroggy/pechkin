package ru.intech.pechkin.messenger.infrastructure.persistence.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.UserRoleMutedPinnedChat;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRoleMutedPinnedChatRepository
        extends MongoRepository<UserRoleMutedPinnedChat, UUID>, UserRoleMutedPinnedChatRepositoryCustom {
    List<UserRoleMutedPinnedChat> findAllByUserId(UUID userId);

    List<UserRoleMutedPinnedChat> findAllByChatId(UUID chatId);

    UserRoleMutedPinnedChat findByUserIdAndChatId(UUID userId, UUID chatId);

    void deleteAllByChatId(UUID chatId);

    void deleteByUserId(UUID userId);

    void deleteByUserIdAndChatId(UUID userId, UUID chatId);
}
