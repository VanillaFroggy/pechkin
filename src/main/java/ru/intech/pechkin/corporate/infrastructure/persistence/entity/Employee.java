package ru.intech.pechkin.corporate.infrastructure.persistence.entity;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@Builder
@Document("employees")
public class Employee {
    @Id
    private UUID id;

    private String fio;

    @Pattern(regexp = "^(\\+7|8)[(]?\\d{3}[)]?[-\\s\\\\.]?\\d{3}[-\\s.]?\\d{4}$")
    private String phoneNumber;

    @Pattern(regexp = "^[\\w.%+-]+@[a-z\\d.-]+\\\\.[a-z]{2,6}$")
    private String email;

    private UUID department;

    private String position;

    private Boolean superuser;

    private Boolean fired;
}
