package ru.intech.pechkin.corporate.ui.web.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.intech.pechkin.corporate.infrastructure.service.EmployeeService;
import ru.intech.pechkin.corporate.infrastructure.service.dto.EmployeeDto;
import ru.intech.pechkin.corporate.infrastructure.service.dto.EmployeeRegistrationResponse;
import ru.intech.pechkin.corporate.ui.web.rest.dto.AddEmployeeRequest;
import ru.intech.pechkin.corporate.ui.web.rest.dto.UpdateEmployeeRequest;
import ru.intech.pechkin.corporate.ui.web.rest.mapper.CorporateRestMapper;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/corporate")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;
    private final CorporateRestMapper mapper;

    @GetMapping("/getAllEmployees")
    public ResponseEntity<List<EmployeeDto>> getEmployeeList() {
        return new ResponseEntity<>(employeeService.getAllEmployees(), HttpStatus.OK);
    }

    @GetMapping("/getEmployeeById/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable("id") UUID employeeId) {
        return new ResponseEntity<>(employeeService.getEmployeeById(employeeId), HttpStatus.OK);
    }

    @GetMapping("/getEmployeesByDepartment/{title}")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByDepartment(@PathVariable("title") String departmentTitle) {
        return new ResponseEntity<>(employeeService.getEmployeesByDepartment(departmentTitle), HttpStatus.OK);
    }

    @GetMapping("/getEmployeesByDepartmentLike/{title}")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByDepartmentLike(@PathVariable("title") String departmentTitle) {
        return new ResponseEntity<>(employeeService.getEmployeesByDepartmentLike(departmentTitle), HttpStatus.OK);
    }

    @GetMapping("/getEmployeesByFioLike/{fio}")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByFioLike(@PathVariable("fio") String fio) {
        return new ResponseEntity<>(employeeService.getEmployeesByFioLike(fio), HttpStatus.OK);
    }

    @GetMapping("/getEmployeesByPositionLike/{position}")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByPositionLike(@PathVariable("position") String position) {
        return new ResponseEntity<>(employeeService.getEmployeesByPositionLike(position), HttpStatus.OK);
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
