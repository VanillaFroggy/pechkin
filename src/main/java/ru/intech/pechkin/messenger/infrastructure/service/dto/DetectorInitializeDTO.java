package ru.intech.pechkin.messenger.infrastructure.service.dto;

import ru.intech.pechkin.messenger.infrastructure.persistance.entity.Detector;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public final class DetectorInitializeDTO {
    @Pattern(regexp = "^[\\w-]{6,50}")
    private String serialNumber;

    @Size(min = 1, max = 50)
    private String model;

    private Detector.ConformityCertificate conformityCertificate;
}
