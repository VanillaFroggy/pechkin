package ru.intech.pechkin.corporate.infrastructure.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UpdateEmployeeDto {
    @NotNull
    private UUID id;

    @NotNull
    private String fio;

    @Pattern(regexp = "^(\\+7|8)[(]?\\d{3}[)]?[-\\s\\\\.]?\\d{3}[-\\s.]?\\d{4}$")
    private String phoneNumber;

    @Pattern(regexp = "^[\\w.%+-]+@[a-z\\d.-]+\\\\.[a-z]{2,6}$")
    private String email;

    private UUID department;

    private String position;

    @NotNull
    private Boolean superuser;

    @NotNull
    private Boolean fired;
}
