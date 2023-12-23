package ru.intech.pechkin.messenger.infrastructure.service.impl;

import ru.intech.pechkin.messenger.infrastructure.persistance.entity.Detector;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.State;
import ru.intech.pechkin.messenger.infrastructure.persistance.repo.DetectorRepository;
import ru.intech.pechkin.messenger.infrastructure.service.DetectorService;
import ru.intech.pechkin.messenger.infrastructure.service.dto.DetectorActivateDTO;
import ru.intech.pechkin.messenger.infrastructure.service.dto.DetectorInitializeDTO;
import ru.intech.pechkin.messenger.infrastructure.service.mapper.DetectorServiceMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DetectorServiceImpl implements DetectorService {
    private final DetectorRepository detectorRepository;
    private final DetectorServiceMapper mapper;

    @Override
    public Detector getDetectorBySerialNumber(@Valid String serialNumber) {
        return detectorRepository
                .findById(serialNumber)
                .orElseThrow(NullPointerException::new);
    }

    @Override
    public void initialize(@Valid DetectorInitializeDTO dto) {
        Detector detectorFromDB = detectorRepository
                .findById(dto.getSerialNumber())
                .orElse(null);
        if (detectorFromDB != null && !detectorFromDB.isNew())
            throw new IllegalArgumentException();
        Detector detector = mapper.initializeDtoToEntity(dto);
        detector.setState(State.SETUP);
        detectorRepository.save(detector);
    }

    @Override
    public void activate(@Valid DetectorActivateDTO dto) {
        Detector detectorFromDB = detectorRepository
                .findById(dto.getSerialNumber())
                .orElseThrow(NullPointerException::new);
        Detector detector = mapper.activateDtoToEntity(dto);
        detector.setState(detectorFromDB.getState());
        if (!detector.isSetup() || isDistanceMoreThanThreeHundred(detector))
            throw new IllegalArgumentException();
        detector.setState(State.ACTIVE);
        detectorRepository.save(detector);
    }

    @Override
    public void setup(String serialNumber) {
        Detector detector = detectorRepository
                .findById(serialNumber)
                .orElseThrow(NullPointerException::new);
        if (!detector.isActive())
            throw new IllegalArgumentException();
        detector.setState(State.SETUP);
        detectorRepository.save(detector);
    }

    @Override
    public void reset(String serialNumber) {
        Detector detector = detectorRepository
                .findById(serialNumber)
                .orElseThrow(NullPointerException::new);
        if (!detector.isSetup())
            throw new IllegalArgumentException();
        detectorRepository.delete(detector);
        detector = Detector.builder()
                .serialNumber(serialNumber)
                .state(State.NEW)
                .build();
        detectorRepository.save(detector);
    }

    @Override
    public List<Detector> getAll() {
        List<Detector> detectors = detectorRepository.findAll();
        if (detectors.isEmpty())
            throw new NoSuchElementException();
        return detectors;
    }

    private boolean isDistanceMoreThanThreeHundred(Detector detector) {
        return Math.abs(
                Math.sqrt(
                        Math.pow((detector.getLocation().getLatitude() - detector.getZone().getGpsCoordinate().getLatitude()), 2)
                                + Math.pow((detector.getLocation().getLongitude() - detector.getZone().getGpsCoordinate().getLongitude()), 2)
                )
        ) > 300;
    }
}
