package ru.intech.pechkin.corporate.infrastructure.service.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class EmployeeDto {
    private UUID id;
    private String fio;
    private String phoneNumber;
    private String email;
    private DepartmentDto department;
    private String position;
    private Boolean superuser;
    private Boolean fired;
}
