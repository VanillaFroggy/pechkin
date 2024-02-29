package ru.intech.pechkin.messenger.infrastructure.persistance.repo;

import org.springframework.stereotype.Repository;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.Message;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepositoryCustom {
    List<Message> findLatestMessagesByChatIdIn(List<UUID> chatIds);
}
