package ru.intech.pechkin.messenger.infrastructure.service.dto.user;

import lombok.Builder;
import lombok.Data;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.Role;

import java.util.UUID;

@Data
@Builder
public class UserWithRoleDto {
    private UUID id;
    private UUID employeeId;
    private String username;
    private String icon;
    private Role role;
    private Boolean blocked;
}
