package ru.intech.pechkin.messenger.ui.web.rest.dto.user;

import lombok.Data;

@Data
public class GetPageOfUsersByFieldLikeRequest {
    private final String value;
    private final int pageNumber;
    private final int pageSize;
}
