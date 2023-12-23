package ru.intech.pechkin.messenger.ui.web.rest.controller;

import ru.intech.pechkin.messenger.infrastructure.persistance.entity.Detector;
import ru.intech.pechkin.messenger.infrastructure.service.DetectorService;
import ru.intech.pechkin.messenger.ui.web.rest.dto.DetectorActiveRequest;
import ru.intech.pechkin.messenger.ui.web.rest.dto.DetectorInitializeRequest;
import ru.intech.pechkin.messenger.ui.web.rest.mapper.DetectorRestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detector")
@RequiredArgsConstructor
public class DetectorController {
    private final DetectorService detectorService;
    private final DetectorRestMapper mapper;

    @GetMapping("")
    public ResponseEntity<List<Detector>> getAllDetectors() {
        List<Detector> detectors = detectorService.getAll();
        return new ResponseEntity<>(detectors, HttpStatus.OK);
    }

    @GetMapping("/{serialNumber}")
    public ResponseEntity<Detector> getDetector(@PathVariable("serialNumber") String serialNumber) {
        Detector detector = detectorService.getDetectorBySerialNumber(serialNumber);
        return new ResponseEntity<>(detector, HttpStatus.OK);
    }

    @PutMapping("/initialized")
    public ResponseEntity<Void> initializeDetector(@RequestBody DetectorInitializeRequest request) {
        detectorService.initialize(mapper.initializeRequestToDto(request));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{serialNumber}/active")
    public ResponseEntity<Void> activateDetector(@PathVariable("serialNumber") String serialNumber,
                                                 @RequestBody DetectorActiveRequest request) {
        detectorService.activate(mapper.activateRequestToDto(serialNumber, request));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{serialNumber}/setup")
    public ResponseEntity<Void> setupDetector(@PathVariable("serialNumber") String serialNumber) {
        detectorService.setup(serialNumber);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{serialNumber}/reset")
    public ResponseEntity<Void> resetDetector(@PathVariable("serialNumber") String serialNumber) {
        detectorService.reset(serialNumber);
        return ResponseEntity.noContent().build();
    }
}
