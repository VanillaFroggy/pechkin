package ru.intech.pechkin.messenger.infrastructure.persistence.repo.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.ChatType;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.UserRoleMutedPinnedChat;
import ru.intech.pechkin.messenger.infrastructure.persistence.repo.UserRoleMutedPinnedChatRepositoryCustom;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRoleMutedPinnedChatRepositoryCustomImpl implements UserRoleMutedPinnedChatRepositoryCustom {
    private final MongoTemplate mongoTemplate;

    @Override
    public List<UserRoleMutedPinnedChat> findByUserIdInAndChatType(List<UUID> userIds, ChatType chatType) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.lookup(
                        "chats",
                        "chatId",
                        "_id",
                        "chats"
                ),
                Aggregation.unwind("chats", true),
                Aggregation.match(
                        Criteria.where("chats.chatType").is(chatType)
                                .and("userId").in(userIds)
                )
        );
        return mongoTemplate.aggregate(
                aggregation,
                "userRoleMutedPinnedChats",
                UserRoleMutedPinnedChat.class
        ).getMappedResults();
    }
}
