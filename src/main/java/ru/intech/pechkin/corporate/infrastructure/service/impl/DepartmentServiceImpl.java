package ru.intech.pechkin.corporate.infrastructure.service.impl;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.intech.pechkin.corporate.infrastructure.persistence.entity.Department;
import ru.intech.pechkin.corporate.infrastructure.persistence.entity.Employee;
import ru.intech.pechkin.corporate.infrastructure.persistence.repo.DepartmentRepository;
import ru.intech.pechkin.corporate.infrastructure.persistence.repo.EmployeeRepository;
import ru.intech.pechkin.corporate.infrastructure.service.DepartmentService;
import ru.intech.pechkin.corporate.infrastructure.service.dto.*;
import ru.intech.pechkin.corporate.infrastructure.service.mapper.CorporateServiceMapper;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.Chat;
import ru.intech.pechkin.messenger.infrastructure.persistence.repo.ChatRepository;
import ru.intech.pechkin.messenger.infrastructure.service.ChatService;
import ru.intech.pechkin.messenger.infrastructure.service.dto.chat.CreateGroupChatDto;

import java.util.List;
import java.util.NoSuchElementException;
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
        List<DepartmentDto> departmentDtos = departmentRepository.findAll()
                .stream()
                .map(mapper::departmentToDto)
                .toList();
        checkListEmptiness(departmentDtos);
        return departmentDtos;
    }

    @Override
    public Page<DepartmentDto> getPageOfDepartments(@Valid GetPageOfDepartmentsDto dto) {
        Page<DepartmentDto> departmentDtos = new PageImpl<>(
                departmentRepository.findAll(PageRequest.of(dto.getPageNumber(), dto.getPageSize()))
                        .stream()
                        .map(mapper::departmentToDto)
                        .toList()
        );
        checkListEmptiness(departmentDtos.getContent());
        return departmentDtos;
    }

    @Override
    public DepartmentDto getDepartmentById(@NotNull UUID departmentId) {
        return mapper.departmentToDto(
                departmentRepository.findById(departmentId)
                        .orElseThrow(NullPointerException::new)
        );
    }

    @Override
    public DepartmentDto getDepartmentByTitle(@NotNull String title) {
        return mapper.departmentToDto(
                departmentRepository.findByTitle(title)
                        .orElseThrow(NullPointerException::new)
        );
    }

    @Override
    public List<DepartmentDto> getDepartmentsByTitleLike(@NotNull String title) {
        List<DepartmentDto> departmentDtos = departmentRepository.findByTitleLikeIgnoreCase(title)
                .stream()
                .map(mapper::departmentToDto)
                .toList();
        checkListEmptiness(departmentDtos);
        return departmentDtos;
    }

    private void checkListEmptiness(List<DepartmentDto> departmentDtos) {
        if (departmentDtos.isEmpty()) {
            throw new NoSuchElementException("There is no departments");
        }
    }

    @Override
    public DepartmentCreationDto createDepartment(@Valid CreateDepartmentDto dto) {
        checkParentBeforeCreationOrChangingDepartment(dto.getParent());
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
        checkParentBeforeCreationOrChangingDepartment(dto.getParent());
        Chat chat = chatRepository.findByDepartmentId(dto.getId())
                .orElseThrow(NullPointerException::new);
        chat.setTitle(dto.getTitle());
        chatRepository.save(chat);
        departmentRepository.save(mapper.updateDepartmentDtoToEntity(dto));
    }

    private void checkParentBeforeCreationOrChangingDepartment(UUID parent) {
        if (parent != null) {
            departmentRepository.findById(parent)
                    .orElseThrow(NullPointerException::new);
        }
    }

    @Override
    public void deleteDepartment(@NotNull UUID departmentId) {
        Page<Employee> employees;
        int counter = 0;
        do {
            employees = employeeRepository.findAllByDepartment(
                    departmentId,
                    PageRequest.of(counter, 2_000)
            );
            employees.forEach(employee -> employee.setDepartment(null));
            employeeRepository.saveAll(employees);
            counter++;
        } while (!employees.isLast());

        chatService.deleteChat(
                chatRepository.findByDepartmentId(departmentId)
                        .orElseThrow(NullPointerException::new)
                        .getId()
        );
        departmentRepository.deleteById(departmentId);
    }
}
