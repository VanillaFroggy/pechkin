package ru.intech.pechkin.messenger.ui.web.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import ru.intech.pechkin.messenger.infrastructure.service.MessageService;
import ru.intech.pechkin.messenger.infrastructure.service.dto.MessageDto;
import ru.intech.pechkin.messenger.ui.web.rest.dto.*;
import ru.intech.pechkin.messenger.ui.web.rest.mapper.MessageRestMapper;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/messenger")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    private final MessageRestMapper mapper;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/getPageOfMessages")
    public ResponseEntity<Page<MessageDto>> getPageOfMessages(@RequestBody GetPageOfMessagesRequest request) {
        return new ResponseEntity<>(
                messageService.getPageOfMessages(mapper.getPageOfMessagesRequestToDto(request)),
                HttpStatus.OK
        );
    }

    @PostMapping("/sendMessage")
    public ResponseEntity<MessageDto> sendMessage(@RequestBody SendMessageRequest request) {
        MessageDto response = messageService.sendMessage(mapper.sendMessageRequestToDto(request));
        sendMessageOverWebSocketByChatId(request.getChatId(), response);
        return new ResponseEntity<>(
                response,
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
    public ResponseEntity<Void> setMessageChecked(@RequestBody SetMessageCheckedRequest request) {
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
    public ResponseEntity<MessageDto> replyToMessage(@RequestBody ReplyToMessageRequest request) {
        MessageDto response = messageService.replyToMessage(mapper.replyToMessageRequestToDto(request));
        sendMessageOverWebSocketByChatId(request.getChatId(), response);
        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @PutMapping("/editMessage")
    public ResponseEntity<Void> editMessage(@RequestBody EditMessageRequest request) {
        MessageDto response = messageService.editMessage(mapper.editMessageRequestToDto(request));
        sendMessageOverWebSocketByChatId(request.getChatId(), response);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deleteMessage")
    public ResponseEntity<Void> deleteChat(@RequestBody DeleteMessageRequest request) {
        messageService.deleteMessage(mapper.deleteMessageRequestToDto(request));
        messagingTemplate.convertAndSend(
                "/topic/chat/" + request.getChatId(),
                "Message with ID " + request.getMessageId() + " has been deleted"
        );
        return ResponseEntity.noContent().build();
    }

    private void sendMessageOverWebSocketByChatId(UUID chatId, MessageDto response) {
        messagingTemplate.convertAndSend("/topic/chat/" + chatId, response);
    }
}
