package ru.intech.pechkin.messenger.ui.web.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.intech.pechkin.messenger.infrastructure.service.ChatService;
import ru.intech.pechkin.messenger.infrastructure.service.dto.ChatCreationResponse;
import ru.intech.pechkin.messenger.infrastructure.service.dto.ChatDto;
import ru.intech.pechkin.messenger.ui.web.rest.dto.CreateGroupChatRequest;
import ru.intech.pechkin.messenger.ui.web.rest.dto.CreateP2PChatRequest;
import ru.intech.pechkin.messenger.ui.web.rest.dto.UpdateChatMutedStatusRequest;
import ru.intech.pechkin.messenger.ui.web.rest.dto.UpdateGroupChatRequest;
import ru.intech.pechkin.messenger.ui.web.rest.mapper.ChatRestMapper;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/messenger")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final ChatRestMapper mapper;

    @GetMapping("/getAllChats/{id}")
    public ResponseEntity<List<ChatDto>> getAllChats(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(chatService.getAllChats(id), HttpStatus.OK);
    }

    @PostMapping("/createFavoritesChat/{id}")
    public ResponseEntity<ChatCreationResponse> createFavoritesChat(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(chatService.createFavoritesChat(id), HttpStatus.CREATED);
    }

    @PostMapping("/createP2PChat")
    public ResponseEntity<ChatCreationResponse> createP2PChat(@RequestBody CreateP2PChatRequest request) {
        return new ResponseEntity<>(
                chatService.createP2PChat(mapper.createP2PChatRequestToDto(request)),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/createGroupChat")
    public ResponseEntity<ChatCreationResponse> createGroupChat(@RequestBody CreateGroupChatRequest request) {
        return new ResponseEntity<>(
                chatService.createGroupChat(mapper.createGroupChatRequestToDto(request)),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/updateGroupChat")
    public ResponseEntity<Void> updateGroupChat(@RequestBody UpdateGroupChatRequest request) {
        chatService.updateGroupChat(mapper.updateGroupChatRequestToDto(request));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/updateChatMutedStatus")
    public ResponseEntity<Void> updateChatMutedStatus(@RequestBody UpdateChatMutedStatusRequest request) {
        chatService.updateChatMutedStatus(mapper.updateChatMutedStatusRequestToDto(request));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deleteChat/{id}")
    public ResponseEntity<Void> deleteChat(@PathVariable("id") UUID id) {
        chatService.deleteChat(id);
        return ResponseEntity.noContent().build();
    }
}
