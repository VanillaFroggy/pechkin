package ru.intech.pechkin.corporate.infrastructure.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import ru.intech.pechkin.corporate.infrastructure.service.dto.*;

import java.util.List;
import java.util.UUID;

@Validated
public interface DepartmentService {
    List<DepartmentDto> getAllDepartments();

    Page<DepartmentDto> getPageOfDepartments(@Valid GetPageOfDepartmentsDto dto);

    DepartmentDto getDepartmentById(@NotNull UUID departmentId);

    DepartmentDto getDepartmentByTitle(@NotNull String title);

    List<DepartmentDto> getDepartmentsByTitleLike(@NotNull String title);

    DepartmentCreationDto createDepartment(@Valid CreateDepartmentDto dto);

    void updateDepartment(@Valid UpdateDepartmentDto dto);

    void deleteDepartment(@NotNull UUID departmentId);
}
