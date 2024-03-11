package ru.intech.pechkin.messenger.ui.web.rest.dto.message;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class FindMessagesByValueRequest {
    private UUID chatId;

    private UUID userId;

    private String value;

    @Size()
    private int pageNumber;

    @Size(min = 1, max = 50)
    private int pageSize;
}
