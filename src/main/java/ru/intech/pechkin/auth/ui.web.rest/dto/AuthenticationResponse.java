package ru.intech.pechkin.auth.ui.web.rest.dto;

import lombok.Builder;
import lombok.Data;
import ru.intech.pechkin.corporate.infrastructure.service.dto.DepartmentDto;

import java.util.UUID;

@Data
@Builder
public class AuthenticationResponse {
    private UUID id;
    private UUID employeeId;
    private String username;
    private String icon;
    private String fio;
    private String phoneNumber;
    private String email;
    private DepartmentDto department;
    private String position;
}
