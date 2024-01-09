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
    private ChatType chatType;
    private String title;
    private String icon;

    public static Chat createFavorites() {
        return Chat.builder()
                .id(UUID.randomUUID())
                .chatType(ChatType.FAVORITES)
                .title("Favorites")
                .icon("favorites.png") // TODO добавить ссылку на базовую иконку Избранного, или попросить прислыть её
                .build();
    }

    public static Chat createP2P() {
        return Chat.builder()
                .id(UUID.randomUUID())
                .chatType(ChatType.P2P)
                .build();
    }

    @NonNull
    public static Chat createGroup(String title, String icon) {
        return Chat.builder()
                .id(UUID.randomUUID())
                .chatType(ChatType.GROUP)
                .title(title)
                .icon(icon)
                .build();
    }
}
