package ru.intech.pechkin.messenger.ui.web.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.intech.pechkin.messenger.infrastructure.service.dto.*;
import ru.intech.pechkin.messenger.ui.web.rest.dto.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface MessageRestMapper {
    GetPageOfMessagesDto getPageOfMessagesRequestToDto(GetPageOfMessagesRequest request);

    SendMessageDto sendMessageRequestToDto(SendMessageRequest request);

    UpdateMessageListDto updateMessageListRequestToDto(UpdateMessageListRequest request);

    SetMessageCheckedDto setMessageCheckedRequestToDto(SetMessageCheckedRequest request);

    FindMessageByValueDto findMessageByValueRequestToDto(FindMessageByValueRequest request);

    ReplyToMessageDto replyToMessageRequestToDto(ReplyToMessageRequest request);

    EditMessageDto editMessageRequestToDto(EditMessageRequest request);

    DeleteMessageDto deleteMessageRequestToDto(DeleteMessageRequest request);
}
