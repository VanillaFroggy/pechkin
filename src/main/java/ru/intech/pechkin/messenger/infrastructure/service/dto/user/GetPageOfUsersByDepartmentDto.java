package ru.intech.pechkin.messenger.infrastructure.service.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class GetPageOfUsersByDepartmentDto {
    private final UUID departmentId;

    @Size
    private final int pageNumber;

    @Size(min = 1, max = 50)
    private final int pageSize;
}
