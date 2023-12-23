package ru.intech.pechkin.messenger.infrastructure.service.dto;


import ru.intech.pechkin.messenger.infrastructure.persistance.entity.GpsCoordinate;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.Zone;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public final class DetectorActivateDTO {
    @Pattern(regexp = "^[\\w-]{6,50}$")
    private String serialNumber;

    @Size(max = 512)
    private String address;

    private GpsCoordinate location;

    private Zone zone;
}
