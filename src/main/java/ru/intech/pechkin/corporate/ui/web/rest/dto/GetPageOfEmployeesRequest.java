package ru.intech.pechkin.corporate.ui.web.rest.dto;

import lombok.Data;

@Data
public class GetPageOfEmployeesRequest {
    private final int pageNumber;
    private final int pageSize;
}
