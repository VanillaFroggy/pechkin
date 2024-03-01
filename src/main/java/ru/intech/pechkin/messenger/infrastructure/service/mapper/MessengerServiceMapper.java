package ru.intech.pechkin.messenger.infrastructure.service.mapper;

import org.mapstruct.*;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.*;
import ru.intech.pechkin.messenger.infrastructure.service.dto.*;

import java.util.List;
import java.util.UUID;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface MessengerServiceMapper {
    UserWithRoleDto userAndRoleToUserWithRoleDto(User user, Role role);

    ChatDto chatToChatDto(Chat chat);

    MessageData messageDataDtoToEntity(UUID id, MessageDataDto dto);

//    @Named("resolvePaymentType")
//    default int resolvePaymentType(Message message) {
//        return message == null ? CASH.getPaymentsCode() : message.getPaymentsCode();
//    }
//    @Mapping(target = "publisher", source = "message", qualifiedByName = "resolvePaymentType")
//    @Mapping(target = "publisher", source = "message", expression = "java(userToDto())")
//
//    default UserDto userToDto(User user) {
//        return null;
//    }

    @Mapping(target = "id", source = "message.id")
    @Mapping(target = "chatId", source = "message.chatId")
    @Mapping(target = "datas", source = "message.datas")
    @Mapping(target = "dateTime", source = "message.dateTime")
    @Mapping(target = "edited", source = "message.edited")
    @Mapping(target = "publisher", source = "publisherDto")
    @Mapping(target = "relatesTo", source = "relatesTo")
    @Mapping(target = "checked", source = "checked")
    MessageDto messageToMessageDto(
            Message message,
            MessagePublisherDto publisherDto,
            Boolean checked,
            MessageDto relatesTo
    );

    SendMessageDto replyToMessageDtoToSendMessageDto(ReplyToMessageDto dto);

    @Mapping(target = "chatId", source = "dto.chatId")
    @Mapping(target = "dateTime", source = "dto.dateTime")
    SendOrReplyToMessageDto sendMessageDtoToSendOrReplyToMessageDto(SendMessageDto dto, Message messageToReply);

    @Mapping(target = "relatesTo", source = "dto.messageToReply")
    Message sendOrReplyToMessageDtoToEntity(
            SendOrReplyToMessageDto dto,
            UUID messageId,
            List<MessageData> datas,
            Boolean edited
    );

    MessagePublisherDto userToMessagePublisherDto(User user);

    UserDto userToUserDto(User user);

}
