package ru.intech.pechkin.messenger.ui.web.rest.dto;

import ru.intech.pechkin.messenger.infrastructure.persistance.entity.Detector;
import lombok.Data;

@Data
public class DetectorInitializeRequest {
    private String serialNumber;
    private String model;
    private Detector.ConformityCertificate conformityCertificate;
}
