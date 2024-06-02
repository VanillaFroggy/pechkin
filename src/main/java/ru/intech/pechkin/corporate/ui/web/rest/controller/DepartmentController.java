package ru.intech.pechkin.corporate.ui.web.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.intech.pechkin.corporate.infrastructure.service.DepartmentService;
import ru.intech.pechkin.corporate.infrastructure.service.dto.DepartmentCreationDto;
import ru.intech.pechkin.corporate.infrastructure.service.dto.DepartmentDto;
import ru.intech.pechkin.corporate.ui.web.rest.dto.CreateDepartmentRequest;
import ru.intech.pechkin.corporate.ui.web.rest.dto.GetPageOfDepartmentsRequest;
import ru.intech.pechkin.corporate.ui.web.rest.dto.UpdateDepartmentRequest;
import ru.intech.pechkin.corporate.ui.web.rest.mapper.CorporateRestMapper;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/corporate")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;
    private final CorporateRestMapper mapper;

    @GetMapping("/getAllDepartments")
    public ResponseEntity<List<DepartmentDto>> getAllDepartments() {
        return new ResponseEntity<>(departmentService.getAllDepartments(), HttpStatus.OK);
    }

    @GetMapping("/getPageOfDepartments")
    public ResponseEntity<Page<DepartmentDto>> getPageOfDepartments(GetPageOfDepartmentsRequest request) {
        return new ResponseEntity<>(
                departmentService.getPageOfDepartments(mapper.getPageOfDepartmentsRequestToDto(request)),
                HttpStatus.OK
        );
    }

    @GetMapping("/getDepartmentById/{id}")
    public ResponseEntity<DepartmentDto> getDepartmentById(@PathVariable("id") UUID departmentId) {
        return new ResponseEntity<>(departmentService.getDepartmentById(departmentId), HttpStatus.OK);
    }

    @GetMapping("/getDepartmentByTitle/{title}")
    public ResponseEntity<DepartmentDto> getDepartmentByTitle(@PathVariable("title") String departmentTitle) {
        return new ResponseEntity<>(departmentService.getDepartmentByTitle(departmentTitle), HttpStatus.OK);
    }

    @GetMapping("/getDepartmentsByTitleLike/{title}")
    public ResponseEntity<List<DepartmentDto>> getDepartmentByTitleLike(@PathVariable("title") String departmentTitle) {
        return new ResponseEntity<>(departmentService.getDepartmentsByTitleLike(departmentTitle), HttpStatus.OK);
    }

    @PostMapping("/createDepartment")
    public ResponseEntity<DepartmentCreationDto> createDepartment(@RequestBody CreateDepartmentRequest request) {
        return new ResponseEntity<>(
                departmentService.createDepartment(mapper.createDepartmentRequestToDto(request)),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/updateDepartment")
    public ResponseEntity<Void> updateDepartment(@RequestBody UpdateDepartmentRequest request) {
        departmentService.updateDepartment(mapper.updateDepartmentRequestToDto(request));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deleteDepartment/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable("id") UUID employeeId) {
        departmentService.deleteDepartment(employeeId);
        return ResponseEntity.noContent().build();
    }
}
