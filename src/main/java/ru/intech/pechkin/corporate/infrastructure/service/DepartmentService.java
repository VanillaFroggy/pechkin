package ru.intech.pechkin.corporate.infrastructure.service;

import jakarta.validation.Valid;
import ru.intech.pechkin.corporate.infrastructure.service.dto.CreateDepartmentDto;
import ru.intech.pechkin.corporate.infrastructure.service.dto.DepartmentCreationDto;
import ru.intech.pechkin.corporate.infrastructure.service.dto.DepartmentDto;
import ru.intech.pechkin.corporate.infrastructure.service.dto.UpdateDepartmentDto;

import java.util.List;
import java.util.UUID;

public interface DepartmentService {
    List<DepartmentDto> getAllDepartments();

    DepartmentDto getDepartmentById(UUID departmentId);

    DepartmentDto getDepartmentByTitle(String title);

    List<DepartmentDto> getDepartmentsByTitleLike(String title);

    DepartmentCreationDto createDepartment(@Valid CreateDepartmentDto dto);

    void updateDepartment(@Valid UpdateDepartmentDto dto);

    void deleteDepartment(UUID departmentId);
}
