package ru.intech.pechkin.messenger.infrastructure.service.dto;

import lombok.Builder;
import lombok.Data;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.Role;

import java.util.UUID;

@Data
@Builder
public class UserWithRoleDto {
    private UUID id;
    private String username;
    private String icon;
    private Role role;
    private String fio;
    private String phoneNumber;
    private String email;
    private String department;
    private String position;
    private Boolean blocked;
}
