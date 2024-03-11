package ru.intech.pechkin.corporate.infrastructure.service;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import ru.intech.pechkin.corporate.infrastructure.service.dto.*;

import java.util.List;
import java.util.UUID;

public interface DepartmentService {
    List<DepartmentDto> getAllDepartments();

    Page<DepartmentDto> getPageOfDepartments(@Valid GetPageOfDepartmentsDto dto);

    DepartmentDto getDepartmentById(UUID departmentId);

    DepartmentDto getDepartmentByTitle(String title);

    List<DepartmentDto> getDepartmentsByTitleLike(String title);

    DepartmentCreationDto createDepartment(@Valid CreateDepartmentDto dto);

    void updateDepartment(@Valid UpdateDepartmentDto dto);

    void deleteDepartment(UUID departmentId);
}
