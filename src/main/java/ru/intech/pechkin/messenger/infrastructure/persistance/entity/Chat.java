package ru.intech.pechkin.messenger.infrastructure.persistance.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@Document("chats")
public class Chat {
    @Id
    private UUID id;
    private ChatType chatType;
    private String title;
    private Map<User, Role> usersWithRole;
    private List<Message> messages = new ArrayList<>();

    @NonNull
    public static Chat createFavorites(User user) {
        return Chat.builder()
                .id(UUID.randomUUID())
                .chatType(ChatType.FAVORITES)
                .title("Favorites")
                .usersWithRole(Map.of(user, Role.ADMIN))
                .build();
    }

    @NonNull
    public static Chat createP2P(Map<User, Role> usersWithRole, Message message) {
        if (usersWithRole.size() != 2) throw new IllegalArgumentException();
        Chat chat = Chat.builder()
                .id(UUID.randomUUID())
                .chatType(ChatType.P2P)
                .usersWithRole(usersWithRole)
                .build();
        chat.addMessage(message);
        return chat;
    }

    @NonNull
    public static Chat createGroup(Map<User, Role> usersWithRole, String title) {
        if (usersWithRole.size() <= 2) throw new IllegalArgumentException();
        return Chat.builder()
                .id(UUID.randomUUID())
                .chatType(ChatType.GROUP)
                .title(title)
                .usersWithRole(usersWithRole)
                .build();
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }
}
