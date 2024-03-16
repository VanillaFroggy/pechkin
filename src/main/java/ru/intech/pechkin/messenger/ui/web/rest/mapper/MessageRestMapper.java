package ru.intech.pechkin.messenger.ui.web.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.intech.pechkin.messenger.infrastructure.service.dto.message.*;
import ru.intech.pechkin.messenger.ui.web.rest.dto.message.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface MessageRestMapper {
    GetPageOfMessagesDto getPageOfMessagesRequestToDto(GetPageOfMessagesRequest request);

    SendMessageDto sendMessageRequestToDto(SendMessageRequest request);

    GetPageOfMessagesAfterLastCheckedMessageDto getPageOfMessagesAfterLastCheckedMessageRequestToDto(
            GetPageOfMessagesAfterLastCheckedMessageRequest request
    );

    GetPageOfMessagesBeforeDateTimeDto getPageOfMessagesBeforeDateTimeRequestToDto(
            GetPageOfMessagesBeforeDateTimeRequest request
    );

    SetMessageCheckedDto setMessageCheckedRequestToDto(SetMessageCheckedRequest request);

    SetMessageListCheckedDto setMessageListCheckedRequestToDto(SetMessageListCheckedRequest request);

    FindMessagesByValueDto findMessagesByValueRequestToDto(FindMessagesByValueRequest request);

    ReplyToMessageDto replyToMessageRequestToDto(ReplyToMessageRequest request);

    EditMessageDto editMessageRequestToDto(EditMessageRequest request);

    DeleteMessageDto deleteMessageRequestToDto(DeleteMessageRequest request);

    DeleteAllMessagesByIdDto deleteAllMessagesByIdRequestToDto(DeleteAllMessagesByIdRequest request);
}
