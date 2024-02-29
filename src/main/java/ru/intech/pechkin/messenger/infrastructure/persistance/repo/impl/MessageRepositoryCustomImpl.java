package ru.intech.pechkin.messenger.infrastructure.persistance.repo.impl;

import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.Message;
import ru.intech.pechkin.messenger.infrastructure.persistance.repo.MessageRepositoryCustom;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MessageRepositoryCustomImpl implements MessageRepositoryCustom {
    private final MongoTemplate mongoTemplate;

    @Override
    public List<Message> findLatestMessagesByChatIdIn(List<UUID> chatIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("chatId").in(chatIds)),
                Aggregation.sort(Sort.Direction.DESC, "dateTime"),
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
                Aggregation.project("id", "chatId", "publisher", "datas", "relatesTo", "edited", "dateTime")
        );
        return mongoTemplate.aggregate(
                        aggregation,
                        "messages",
                        Message.class
                )
                .getMappedResults();
    }
}
