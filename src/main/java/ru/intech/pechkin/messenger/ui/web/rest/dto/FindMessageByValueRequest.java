package ru.intech.pechkin.messenger.ui.web.rest.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class FindMessageByValueRequest {
    private UUID chatId;
    private String value;
}
