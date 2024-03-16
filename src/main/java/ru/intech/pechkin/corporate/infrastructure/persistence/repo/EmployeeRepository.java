package ru.intech.pechkin.corporate.infrastructure.persistence.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import ru.intech.pechkin.corporate.infrastructure.persistence.entity.Employee;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, UUID> {
    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByPhoneNumber(String phoneNumber);

    Page<Employee> findAllByDepartment(UUID department, Pageable pageable);

    Page<Employee> findAllByDepartmentIn(Collection<UUID> departments, Pageable pageable);

    @Query(value = "{ 'fio' : { $regex: ?0, $options: 'i' } }", count = true)
    Page<Employee> findByFioLikeIgnoreCase(String fio, Pageable pageable);

    @Query(value = "{ 'position' : { $regex: ?0, $options: 'i' } }", count = true)
    Page<Employee> findByPositionLikeIgnoreCase(String fio, Pageable pageable);
}
