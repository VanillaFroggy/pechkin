package ru.intech.pechkin.corporate.infrastructure.service;

import ru.intech.pechkin.corporate.infrastructure.persistance.entity.Employee;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {
    List<Employee> getEmployeeList();

    Employee getEmployeeById(UUID employeeId);

//    void hireEmployee(HireEmployeeDto dto);
//
//    void updateEmployee(UpdateEmployeeDto dto);

    void fireEmployee(UUID employeeId);
}
