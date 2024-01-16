package ru.intech.pechkin.messenger.ui.web.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.intech.pechkin.messenger.infrastructure.service.MessageService;
import ru.intech.pechkin.messenger.infrastructure.service.dto.ChatCreationResponse;
import ru.intech.pechkin.messenger.infrastructure.service.dto.MessageDto;
import ru.intech.pechkin.messenger.infrastructure.service.dto.MessageSendingResponse;
import ru.intech.pechkin.messenger.ui.web.rest.dto.*;
import ru.intech.pechkin.messenger.ui.web.rest.mapper.MessageRestMapper;

@RestController
@RequestMapping("/api/v1/messenger")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    private final MessageRestMapper mapper;

    @GetMapping("/getPageOfMessages")
    public ResponseEntity<Page<MessageDto>> getPageOfMessages(@RequestBody GetPageOfMessagesRequest request) {
        return new ResponseEntity<>(
                messageService.getPageOfMessages(mapper.getPageOfMessagesRequestToDto(request)),
                HttpStatus.OK
        );
    }

    @PostMapping("/sendMessage")
    public ResponseEntity<MessageSendingResponse> sendMessage(@RequestBody SendMessageRequest request) {
        return new ResponseEntity<>(
                messageService.sendMessage(mapper.sendMessageRequestToDto(request)),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/updateMessageList")
    public ResponseEntity<Page<MessageDto>> updateMessageList(@RequestBody UpdateMessageListRequest request) {
        return new ResponseEntity<>(
                messageService.updateMessageList(mapper.updateMessageListRequestToDto(request)),
                HttpStatus.OK
        );
    }

    @PutMapping("/setMessageChecked")
    public ResponseEntity<ChatCreationResponse> setMessageChecked(@RequestBody SetMessageCheckedRequest request) {
        messageService.setMessageChecked(mapper.setMessageCheckedRequestToDto(request));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/findMessageByValue")
    public ResponseEntity<Page<MessageDto>> findMessageByValue(@RequestBody FindMessageByValueRequest request) {
        return new ResponseEntity<>(
                messageService.findMessageByValue(mapper.findMessageByValueRequestToDto(request)),
                HttpStatus.OK
        );
    }

    @PostMapping("/replyToMessage")
    public ResponseEntity<MessageSendingResponse> replyToMessage(@RequestBody ReplyToMessageRequest request) {
        return new ResponseEntity<>(
                messageService.replyToMessage(mapper.replyToMessageRequestToDto(request)),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/editMessage")
    public ResponseEntity<ChatCreationResponse> editMessage(@RequestBody EditMessageRequest request) {
        messageService.editMessage(mapper.editMessageRequestToDto(request));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deleteMessage")
    public ResponseEntity<Void> deleteChat(@RequestBody DeleteMessageRequest request) {
        messageService.deleteMessage(mapper.deleteMessageRequestToDto(request));
        return ResponseEntity.noContent().build();
    }
}
