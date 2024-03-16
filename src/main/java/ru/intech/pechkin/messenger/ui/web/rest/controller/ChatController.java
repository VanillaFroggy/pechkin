package ru.intech.pechkin.messenger.ui.web.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import ru.intech.pechkin.messenger.infrastructure.service.ChatService;
import ru.intech.pechkin.messenger.infrastructure.service.dto.chat.ChatDto;
import ru.intech.pechkin.messenger.infrastructure.service.dto.user.UserWithRoleDto;
import ru.intech.pechkin.messenger.ui.web.rest.dto.chat.GetP2PChatByUsersRequest;
import ru.intech.pechkin.messenger.ui.web.rest.dto.chat.*;
import ru.intech.pechkin.messenger.ui.web.rest.mapper.ChatRestMapper;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/messenger")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final ChatRestMapper mapper;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/getPageOfChats")
    public ResponseEntity<Page<ChatDto>> getPageOfChats(@RequestBody GetPageOfChatsRequest request) {
        return new ResponseEntity<>(
                chatService.getPageOfChats(mapper.getPageOfChatsRequestToDto(request)),
                HttpStatus.OK
        );
    }

    @GetMapping("/getChatByIdAndUserId")
    public ResponseEntity<ChatDto> getChatByIdAndUserId(@RequestBody GetChatByIdAndUserIdRequest request) {
        return new ResponseEntity<>(
                chatService.getChatByIdAndUserId(mapper.getChatByIdAndUserIdRequestToDto(request)),
                HttpStatus.OK
        );
    }

    @GetMapping("/getP2PChatByUsers")
    public ResponseEntity<ChatDto> getP2PChatByUsers(@RequestBody GetP2PChatByUsersRequest request) {
        return new ResponseEntity<>(
                chatService.getP2PChatByUsers(mapper.getP2PChatByUsersRequestToDto(request)),
                HttpStatus.OK
        );
    }

    @PostMapping("/createFavoritesChat/{id}")
    public ResponseEntity<ChatCreationResponse> createFavoritesChat(@PathVariable("id") UUID id) {
        ChatDto chatDto = chatService.createFavoritesChat(id);
        sendChatOverWebSocket(chatDto);
        return new ResponseEntity<>(new ChatCreationResponse(chatDto.getId()), HttpStatus.CREATED);
    }

    @PostMapping("/createP2PChat")
    public ResponseEntity<ChatCreationResponse> createP2PChat(@RequestBody CreateP2PChatRequest request) {
        ChatDto chatDto = chatService.createP2PChat(mapper.createP2PChatRequestToDto(request));
        sendChatOverWebSocket(chatDto);
        return new ResponseEntity<>(new ChatCreationResponse(chatDto.getId()), HttpStatus.CREATED);
    }

    @PostMapping("/createGroupChat")
    public ResponseEntity<ChatCreationResponse> createGroupChat(@RequestBody CreateGroupChatRequest request) {
        ChatDto chatDto = chatService.createGroupChat(mapper.createGroupChatRequestToDto(request));
        sendChatOverWebSocket(chatDto);
        return new ResponseEntity<>(new ChatCreationResponse(chatDto.getId()), HttpStatus.CREATED);
    }

    @PutMapping("/updateGroupChat")
    public ResponseEntity<Void> updateGroupChat(@RequestBody UpdateGroupChatRequest request) {
        ChatDto chatDto = chatService.updateGroupChat(mapper.updateGroupChatRequestToDto(request));
        sendChatOverWebSocket(chatDto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/updateGroupChatTitle")
    public ResponseEntity<Void> updateGroupChatTitle(@RequestBody UpdateGroupChatTitleOrIconRequest request) {
        chatService.updateGroupChatTitle(mapper.updateGroupChatTitleOrIconRequestToDto(request));
        sendMessageByChatOverWebSocket(request.getChatId(), "Chat's title now is " + request.getValue());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/updateGroupChatIcon")
    public ResponseEntity<Void> updateGroupChatIcon(@RequestBody UpdateGroupChatTitleOrIconRequest request) {
        chatService.updateGroupChatIcon(mapper.updateGroupChatTitleOrIconRequestToDto(request));
        sendMessageByChatOverWebSocket(request.getChatId(), "Chat's icon now is " + request.getValue());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/addUserToGroupChat")
    public ResponseEntity<Void> addUserToGroupChat(@RequestBody UpdateUserInGroupChatRequest request) {
        sendUserWithRoleDtoByChatOverWebSocket(
                request.getChatId(),
                chatService.addUserToGroupChat(mapper.updateUserInGroupChatRequestToDto(request))
        );
        sendMessageByUserOverWebSocket(
                request.getUserId(),
                "You were added to chat with ID " + request.getChatId()
        );
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/updateUserInGroupChat")
    public ResponseEntity<Void> updateUserInGroupChat(@RequestBody UpdateUserInGroupChatRequest request) {
        sendUserWithRoleDtoByChatOverWebSocket(
                request.getChatId(),
                chatService.updateUserInGroupChat(mapper.updateUserInGroupChatRequestToDto(request))
        );
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/removeUserFromGroupChat")
    public ResponseEntity<Void> removeUserFromGroupChat(@RequestBody UpdateUserInGroupChatRequest request) {
        sendUserWithRoleDtoByChatOverWebSocket(
                request.getChatId(),
                chatService.removeUserFromGroupChat(mapper.updateUserInGroupChatRequestToDto(request))
        );
        sendMessageByUserOverWebSocket(
                request.getUserId(),
                "You were removed from chat with ID " + request.getChatId()
        );
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/updateChatMutedStatus")
    public ResponseEntity<Void> updateChatMutedStatus(@RequestBody UpdateChatMutedOrPinnedStatusRequest request) {
        chatService.updateChatMutedStatus(mapper.updateChatMutedOrPinnedStatusRequestToDto(request));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/updateChatPinnedStatus")
    public ResponseEntity<Void> updateChatPinnedStatus(@RequestBody UpdateChatMutedOrPinnedStatusRequest request) {
        chatService.updateChatPinnedStatus(mapper.updateChatMutedOrPinnedStatusRequestToDto(request));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deleteChat/{id}")
    public ResponseEntity<Void> deleteChat(@PathVariable("id") UUID id) {
        chatService.deleteChat(id).forEach(userId ->
                sendMessageByUserOverWebSocket(
                        userId,
                        "Chat with ID " + id + " has been deleted"
                )
        );
        return ResponseEntity.noContent().build();
    }

    private void sendChatOverWebSocket(ChatDto chatDto) {
        chatDto.getUsersWithRole().forEach(userWithRoleDto ->
                messagingTemplate.convertAndSend("/topic/user/" + userWithRoleDto.getId(), chatDto)
        );
    }

    private void sendMessageByChatOverWebSocket(UUID chatId, String message) {
        messagingTemplate.convertAndSend("/topic/chat/" + chatId, message);
    }

    private void sendUserWithRoleDtoByChatOverWebSocket(UUID chatId, UserWithRoleDto dto) {
        messagingTemplate.convertAndSend("/topic/chat/" + chatId, dto);
    }

    private void sendMessageByUserOverWebSocket(UUID userId, String message) {
        messagingTemplate.convertAndSend("/topic/user/" + userId, message);
    }
}
