package ru.intech.pechkin.messenger.ui.web.rest.controller;

import ru.intech.pechkin.messenger.infrastructure.service.ChatService;
import ru.intech.pechkin.messenger.infrastructure.service.dto.ChatDto;
import ru.intech.pechkin.messenger.ui.web.rest.dto.*;
import ru.intech.pechkin.messenger.ui.web.rest.mapper.ChatRestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messenger")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final ChatRestMapper mapper;

    @GetMapping("/getAllChats/{id}")
    public ResponseEntity<List<ChatDto>> getAllChats(@PathVariable("id") UUID id) {
        List<ChatDto> chats = chatService.getAllChats(id);
        return new ResponseEntity<>(chats, HttpStatus.OK);
    }

    @PostMapping("/createFavoritesChat/{id}")
    public ResponseEntity<Void> createFavoritesChat(@PathVariable("id") UUID id) {
        chatService.createFavoritesChat(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/createP2PChat")
    public ResponseEntity<Void> createP2PChat(@RequestBody CreateP2PChatRequest request) {
        chatService.createP2PChat(mapper.createP2PChatRequestToDto(request));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/createGroupChat")
    public ResponseEntity<Void> createGroupChat(@RequestBody CreateGroupChatRequest request) {
        chatService.createGroupChat(mapper.createGroupChatRequestToDto(request));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/updateGroupChat")
    public ResponseEntity<Void> updateGroupChat(@RequestBody UpdateGroupChatRequest request) {
        chatService.updateGroupChat(mapper.updateGroupChatRequestToDto(request));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deleteChat/{id}")
    public ResponseEntity<Void> deleteChat(@PathVariable("id") UUID id) {
        chatService.deleteChat(id);
        return ResponseEntity.noContent().build();
    }
}
