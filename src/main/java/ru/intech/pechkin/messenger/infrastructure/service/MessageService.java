package ru.intech.pechkin.messenger.infrastructure.service;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import ru.intech.pechkin.messenger.infrastructure.service.dto.message.*;

public interface MessageService {
    Page<MessageDto> getPageOfMessages(@Valid GetPageOfMessagesDto dto);

    MessageDto sendMessage(@Valid SendMessageDto dto);

    Page<MessageDto> getPageOfMessagesAfterLastCheckedMessage(
            @Valid GetPageOfMessagesAfterLastCheckedMessageDto dto
    );

    Page<MessageDto> getPageOfMessagesBeforeDateTime(@Valid GetPageOfMessagesBeforeDateTimeDto dto);

    void setMessageChecked(SetMessageCheckedDto dto);

    void setMessageListChecked(SetMessageListCheckedDto dto);

    Page<MessageDto> findMessagesByValue(@Valid FindMessagesByValueDto dto);

    MessageDto replyToMessage(@Valid ReplyToMessageDto dto);

    MessageDto editMessage(@Valid EditMessageDto dto);

    void deleteMessage(DeleteMessageDto dto);

    void deleteAllMessagesById(DeleteAllMessagesByIdDto dto);
}
