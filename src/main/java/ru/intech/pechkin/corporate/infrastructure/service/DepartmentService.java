package ru.intech.pechkin.corporate.infrastructure.service;

import ru.intech.pechkin.corporate.infrastructure.persistence.entity.Department;

import java.util.List;
import java.util.UUID;

public interface DepartmentService {
    List<Department> getAllDepartments();

    Department getDepartmentById(UUID departmentId);

//    void createDepartment(CreateDepartmentDto dto);
//
//    void updateDepartment(UpdateDepartmentDto dto);

    void deleteDepartment(UUID departmentId);
}
