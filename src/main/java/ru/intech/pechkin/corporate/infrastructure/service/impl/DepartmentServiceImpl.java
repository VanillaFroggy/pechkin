package ru.intech.pechkin.corporate.infrastructure.service.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.intech.pechkin.corporate.infrastructure.persistence.entity.Department;
import ru.intech.pechkin.corporate.infrastructure.persistence.entity.Employee;
import ru.intech.pechkin.corporate.infrastructure.persistence.repo.DepartmentRepository;
import ru.intech.pechkin.corporate.infrastructure.persistence.repo.EmployeeRepository;
import ru.intech.pechkin.corporate.infrastructure.service.DepartmentService;
import ru.intech.pechkin.corporate.infrastructure.service.dto.CreateDepartmentDto;
import ru.intech.pechkin.corporate.infrastructure.service.dto.DepartmentCreationDto;
import ru.intech.pechkin.corporate.infrastructure.service.dto.DepartmentDto;
import ru.intech.pechkin.corporate.infrastructure.service.dto.UpdateDepartmentDto;
import ru.intech.pechkin.corporate.infrastructure.service.mapper.CorporateServiceMapper;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.Chat;
import ru.intech.pechkin.messenger.infrastructure.persistence.repo.ChatRepository;
import ru.intech.pechkin.messenger.infrastructure.service.ChatService;
import ru.intech.pechkin.messenger.infrastructure.service.dto.CreateGroupChatDto;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final ChatRepository chatRepository;
    private final ChatService chatService;
    private final CorporateServiceMapper mapper;

    @Override
    public List<DepartmentDto> getAllDepartments() {
        return departmentRepository.findAll()
                .stream()
                .map(mapper::departmentToDto)
                .toList();
    }

    @Override
    public DepartmentDto getDepartmentById(UUID departmentId) {
        return mapper.departmentToDto(
                departmentRepository.findById(departmentId)
                        .orElseThrow(NullPointerException::new)
        );
    }

    @Override
    public DepartmentDto getDepartmentByTitle(String title) {
        return mapper.departmentToDto(
                departmentRepository.findByTitle(title)
                        .orElseThrow(NullPointerException::new)
        );
    }

    @Override
    public List<DepartmentDto> getDepartmentsByTitleLike(String title) {
        return departmentRepository.findByTitleLikeIgnoreCase(title)
                .stream()
                .map(mapper::departmentToDto)
                .toList();
    }

    @Override
    public DepartmentCreationDto createDepartment(@Valid CreateDepartmentDto dto) {
        Department department = mapper.createDepartmentDtoToEntity(UUID.randomUUID(), dto);
        departmentRepository.save(department);
        chatService.createGroupChat(
                CreateGroupChatDto.builder()
                        .departmentId(department.getId())
                        .title(department.getTitle())
                        .corporate(true)
                        .build()
        );
        return new DepartmentCreationDto(department.getId());
    }

    @Override
    public void updateDepartment(@Valid UpdateDepartmentDto dto) {
        departmentRepository.findById(dto.getId())
                        .orElseThrow(NullPointerException::new);
        Chat chat = chatRepository.findByDepartmentId(dto.getId())
                .orElseThrow(NullPointerException::new);
        chat.setTitle(dto.getTitle());
        chatRepository.save(chat);
        departmentRepository.save(mapper.updateDepartmentDtoToEntity(dto));
    }

    @Override
    public void deleteDepartment(UUID departmentId) {
        List<Employee> employees = employeeRepository.findAllByDepartment(departmentId);
        employees.forEach(employee -> employee.setDepartment(null));
        employeeRepository.saveAll(employees);
        chatService.deleteChat(
                chatRepository.findByDepartmentId(departmentId)
                        .orElseThrow(NullPointerException::new)
                        .getId()
        );
        departmentRepository.deleteById(departmentId);
    }
}
