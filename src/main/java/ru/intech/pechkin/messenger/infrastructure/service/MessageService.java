package ru.intech.pechkin.messenger.infrastructure.service;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import ru.intech.pechkin.messenger.infrastructure.service.dto.*;

public interface MessageService {

    Page<MessageDto> getPageOfMessages(@Valid GetPageOfMessagesDto dto);

    MessageSendingResponse sendMessage(@Valid SendMessageDto dto);

    Page<MessageDto> updateMessageList(@Valid UpdateMessageListDto dto);

    void setMessageChecked(SetMessageCheckedDto dto);

    Page<MessageDto> findMessageByValue(@Valid FindMessageByValueDto dto);

    MessageSendingResponse replyToMessage(@Valid ReplyToMessageDto dto);

    void editMessage(@Valid EditMessageDto dto);

    void deleteMessage(DeleteMessageDto dto);
}
