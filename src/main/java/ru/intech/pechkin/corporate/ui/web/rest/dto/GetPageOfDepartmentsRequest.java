package ru.intech.pechkin.corporate.ui.web.rest.dto;

import lombok.Data;

@Data
public class GetPageOfDepartmentsRequest {
    private final int pageNumber;
    private final int pageSize;
}
