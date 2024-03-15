package ru.intech.pechkin.messenger.ui.web.rest.dto.user;

import lombok.Data;

import java.util.UUID;

@Data
public class GetPageOfUsersByDepartmentRequest {
    private final UUID departmentId;
    private final int pageNumber;
    private final int pageSize;
}
