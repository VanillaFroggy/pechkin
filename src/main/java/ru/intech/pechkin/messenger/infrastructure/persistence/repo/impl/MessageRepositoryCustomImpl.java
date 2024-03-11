package ru.intech.pechkin.messenger.infrastructure.persistence.repo.impl;

import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.Message;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.UserRoleMutedPinnedChat;
import ru.intech.pechkin.messenger.infrastructure.persistence.repo.MessageRepositoryCustom;

import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MessageRepositoryCustomImpl implements MessageRepositoryCustom {
    private final MongoTemplate mongoTemplate;

    @Override
    public Page<Message> findLatestMessagesByChatIdIn(
            List<UserRoleMutedPinnedChat> userRoleMutedPinnedChats,
            Pageable pageable
    ) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("chatId").in(
                        userRoleMutedPinnedChats.parallelStream()
                                .map(UserRoleMutedPinnedChat::getChatId)
                                .distinct()
                                .toList()
                )),
                Aggregation.sort(Sort.Direction.DESC, "userRoleMutedPinnedChats.pinned", "dateTime"),
                Aggregation.group("chatId")
                        .first("_id").as("id")
                        .first("publisher").as("publisher")
                        .first("datas").as("datas")
                        .first("relatesTo").as("relatesTo")
                        .first("edited").as("edited")
                        .first("dateTime").as("dateTime"),
                Aggregation.replaceRoot()
                        .withDocument(Document.parse(
                                "{_id: '$id', chatId: '$_id', publisher: '$publisher', datas: '$datas'," +
                                        " relatesTo: '$relatesTo', edited: '$edited', dateTime: '$dateTime'}"
                        )),
                Aggregation.lookup(
                        "userRoleMutedPinnedChats",
                        "chatId",
                        "chatId",
                        "userRoleMutedPinnedChats"
                ),
                Aggregation.sort(Sort.Direction.DESC, "userRoleMutedPinnedChats.pinned", "dateTime"),
                Aggregation.project("id", "chatId", "publisher", "datas", "relatesTo", "edited", "dateTime")
        ).withOptions(
                Aggregation.newAggregationOptions()
                        .allowDiskUse(true)
                        .build()
        );

        List<Message> messages = mongoTemplate.aggregate(
                        aggregation,
                        "messages",
                        Message.class
                ).getMappedResults();

        int startItem = pageable.getPageSize() * pageable.getPageNumber();

        List<Message> pageList;

        if (messages.size() < startItem) {
            pageList = Collections.emptyList();
        } else {
            pageList = messages.subList(
                    startItem,
                    Math.min(startItem + pageable.getPageSize(), messages.size())
            );
        }

        return new PageImpl<>(
                pageList,
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()),
                messages.size()
        );
    }
}
