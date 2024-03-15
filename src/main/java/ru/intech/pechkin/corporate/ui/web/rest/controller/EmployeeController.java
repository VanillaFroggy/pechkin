package ru.intech.pechkin.corporate.ui.web.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.intech.pechkin.corporate.infrastructure.service.EmployeeService;
import ru.intech.pechkin.corporate.infrastructure.service.dto.EmployeeDto;
import ru.intech.pechkin.corporate.infrastructure.service.dto.EmployeeRegistrationResponse;
import ru.intech.pechkin.corporate.ui.web.rest.dto.*;
import ru.intech.pechkin.corporate.ui.web.rest.mapper.CorporateRestMapper;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/corporate")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;
    private final CorporateRestMapper mapper;

    @GetMapping("/getPageOfEmployees")
    public ResponseEntity<Page<EmployeeDto>> getPageOfEmployees(@RequestBody GetPageOfEmployeesRequest request) {
        return new ResponseEntity<>(
                employeeService.getPageOfEmployees(mapper.getPageOfEmployeesRequestToDto(request)),
                HttpStatus.OK
        );
    }

    @GetMapping("/getEmployeeById/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable("id") UUID employeeId) {
        return new ResponseEntity<>(employeeService.getEmployeeById(employeeId), HttpStatus.OK);
    }

    @GetMapping("/getPageOfEmployeesByDepartment")
    public ResponseEntity<Page<EmployeeDto>> getPageOfEmployeesByDepartment(
            @RequestBody GetPageOfEmployeesByDepartmentRequest request
    ) {
        return new ResponseEntity<>(
                employeeService.getPageOfEmployeesByDepartment(
                        mapper.getPageOfEmployeesByDepartmentRequestToDto(request)
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/getPageOfEmployeesByDepartmentLike")
    public ResponseEntity<Page<EmployeeDto>> getPageOfEmployeesByDepartmentLike(
            @RequestBody GetPageOfEmployeesByFieldLikeRequest request
    ) {
        return new ResponseEntity<>(
                employeeService.getPageOfEmployeesByDepartmentLike(
                        mapper.getPageOfEmployeesByFieldLikeRequestToDto(request)
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/getPageOfEmployeesByFioLike")
    public ResponseEntity<Page<EmployeeDto>> getPageOfEmployeesByFioLike(
            @RequestBody GetPageOfEmployeesByFieldLikeRequest request
    ) {
        return new ResponseEntity<>(
                employeeService.getPageOfEmployeesByFioLike(
                        mapper.getPageOfEmployeesByFieldLikeRequestToDto(request)
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/getPageOfEmployeesByPositionLike")
    public ResponseEntity<Page<EmployeeDto>> getPageOfEmployeesByPositionLike(
            @RequestBody GetPageOfEmployeesByFieldLikeRequest request
    ) {
        return new ResponseEntity<>(
                employeeService.getPageOfEmployeesByPositionLike(
                        mapper.getPageOfEmployeesByFieldLikeRequestToDto(request)
                ),
                HttpStatus.OK
        );
    }

    @PostMapping("/addEmployee")
    public ResponseEntity<EmployeeRegistrationResponse> addEmployee(@RequestBody AddEmployeeRequest request) {
        return new ResponseEntity<>(
                employeeService.addEmployee(mapper.addEmployeeRequestToDto(request)),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/updateEmployee")
    public ResponseEntity<Void> updateEmployee(@RequestBody UpdateEmployeeRequest request) {
        employeeService.updateEmployee(mapper.updateEmployeeRequestToDto(request));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/fireEmployee/{id}")
    public ResponseEntity<Void> fireEmployee(@PathVariable("id") UUID employeeId) {
        employeeService.fireEmployee(employeeId);
        return ResponseEntity.noContent().build();
    }
}
