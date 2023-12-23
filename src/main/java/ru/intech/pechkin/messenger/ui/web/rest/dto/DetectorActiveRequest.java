package ru.intech.pechkin.messenger.ui.web.rest.dto;

import ru.intech.pechkin.messenger.infrastructure.persistance.entity.GpsCoordinate;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.Zone;
import lombok.Data;

@Data
public class DetectorActiveRequest {
    private String address;
    private GpsCoordinate location;
    private Zone zone;
}
