package ru.intech.pechkin.corporate.ui.web.rest.dto;

import lombok.Data;

@Data
public class GetPageOfEmployeesByFieldLikeRequest {
    private final String value;
    private final int pageNumber;
    private final int pageSize;
}
