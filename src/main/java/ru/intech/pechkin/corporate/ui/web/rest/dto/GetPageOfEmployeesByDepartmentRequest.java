package ru.intech.pechkin.corporate.ui.web.rest.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class GetPageOfEmployeesByDepartmentRequest {
    private final UUID departmentId;
    private final int pageNumber;
    private final int pageSize;
}
