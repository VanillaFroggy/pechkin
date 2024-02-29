package ru.intech.pechkin.messenger.infrastructure.persistance.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@Builder
@Document("chats")
public class Chat {
    @Id
    private UUID id;
    private UUID departmentId;
    private ChatType chatType;
    private String title;
    private String icon;
    private Boolean corporate;

    public static Chat createFavorites() {
        return Chat.builder()
                .id(UUID.randomUUID())
                .chatType(ChatType.FAVORITES)
                .title("Favorites")
                .corporate(false)
                .build();
    }

    public static Chat createP2P() {
        return Chat.builder()
                .id(UUID.randomUUID())
                .chatType(ChatType.P2P)
                .corporate(false)
                .build();
    }

    @NonNull
    public static Chat createGroup(String title, String icon, Boolean corporate, UUID departmentId) {
        return Chat.builder()
                .id(UUID.randomUUID())
                .departmentId(departmentId)
                .chatType(ChatType.GROUP)
                .title(title)
                .icon(icon)
                .corporate(corporate)
                .build();
    }
}
