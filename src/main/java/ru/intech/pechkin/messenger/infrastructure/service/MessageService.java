package ru.intech.pechkin.messenger.infrastructure.service;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import ru.intech.pechkin.messenger.infrastructure.service.dto.*;

public interface MessageService {

    Page<MessageDto> getPageOfMessages(@Valid GetPageOfMessagesDto dto);

    MessageDto sendMessage(@Valid SendMessageDto dto);

    void setMessageChecked(SetMessageCheckedDto dto);

    Page<MessageDto> findMessageByValue(@Valid FindMessageByValueDto dto);

    MessageDto replyToMessage(@Valid ReplyToMessageDto dto);

    MessageDto editMessage(@Valid EditMessageDto dto);

    void deleteMessage(DeleteMessageDto dto);
}
