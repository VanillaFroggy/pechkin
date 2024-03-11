package ru.intech.pechkin.messenger.infrastructure.service.mapper;

import org.mapstruct.*;
import ru.intech.pechkin.corporate.infrastructure.service.dto.EmployeeDto;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.*;
import ru.intech.pechkin.messenger.infrastructure.service.dto.chat.ChatDto;
import ru.intech.pechkin.messenger.infrastructure.service.dto.message.*;
import ru.intech.pechkin.messenger.infrastructure.service.dto.user.UserDto;
import ru.intech.pechkin.messenger.infrastructure.service.dto.user.UserWithRoleDto;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface MessengerServiceMapper {
    UserWithRoleDto userAndRoleToUserWithRoleDto(User user, Role role);

    ChatDto chatToChatDto(Chat chat);

    MessageData messageDataDtoToEntity(UUID id, MessageDataDto dto);

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
    SendOrReplyToMessageDto sendMessageDtoToSendOrReplyToMessageDto(SendMessageDto dto, Message messageToReply);

    @Mapping(target = "id", source = "messageId")
    @Mapping(target = "publisher", source = "dto.userId")
    @Mapping(target = "relatesTo", source = "dto.messageToReply")
    Message sendOrReplyToMessageDtoToEntity(
            SendOrReplyToMessageDto dto,
            UUID messageId,
            List<MessageData> datas,
            ZonedDateTime dateTime,
            Boolean edited
    );

    MessagePublisherDto userToMessagePublisherDto(User user);

    @Mapping(target = "id", source = "user.id")
    UserDto userAndEployeeDtoToUserDto(User user, EmployeeDto employeeDto);
}
