package ru.intech.pechkin.corporate.infrastructure.service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class EmployeeRegistrationResponse {
    private final String registrationLink;
}
