package ru.intech.pechkin.messenger.infrastructure.service;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import ru.intech.pechkin.messenger.infrastructure.service.dto.message.*;

@Validated
public interface MessageService {
    Page<MessageDto> getPageOfMessages(@Valid GetPageOfMessagesDto dto);

    MessageDto sendMessage(@Valid SendMessageDto dto);

    Page<MessageDto> getPageOfMessagesAfterLastCheckedMessage(@Valid GetPageOfMessagesAfterLastCheckedMessageDto dto);

    Page<MessageDto> getPageOfMessagesBeforeDateTime(@Valid GetPageOfMessagesBeforeDateTimeDto dto);

    void setMessageChecked(@Valid SetMessageCheckedDto dto);

    void setMessageListChecked(@Valid SetMessageListCheckedDto dto);

    Page<MessageDto> findMessagesByValue(@Valid FindMessagesByValueDto dto);

    MessageDto replyToMessage(@Valid ReplyToMessageDto dto);

    MessageDto editMessage(@Valid EditMessageDto dto);

    void deleteMessage(@Valid DeleteMessageDto dto);

    void deleteAllMessagesById(@Valid DeleteAllMessagesByIdDto dto);
}
