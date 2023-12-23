package ru.intech.pechkin.messenger.infrastructure.service.mapper;

import ru.intech.pechkin.messenger.infrastructure.persistance.entity.Detector;
import ru.intech.pechkin.messenger.infrastructure.service.dto.DetectorActivateDTO;
import ru.intech.pechkin.messenger.infrastructure.service.dto.DetectorInitializeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface DetectorServiceMapper {
    @Mapping(target = "serialNumber", source = "serialNumber")
    @Mapping(target = "model", source = "model")
    @Mapping(target = "conformityCertificate", source = "conformityCertificate")
    Detector initializeDtoToEntity(DetectorInitializeDTO dto);

    @Mapping(target = "serialNumber", source = "serialNumber")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "location", source = "location")
    @Mapping(target = "zone", source = "zone")
    Detector activateDtoToEntity(DetectorActivateDTO dto);
}
