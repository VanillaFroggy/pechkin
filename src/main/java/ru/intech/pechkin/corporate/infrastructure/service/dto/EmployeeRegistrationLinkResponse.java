package ru.intech.pechkin.corporate.infrastructure.service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class EmployeeRegistrationLinkResponse {
    private final String registrationLink;
}
