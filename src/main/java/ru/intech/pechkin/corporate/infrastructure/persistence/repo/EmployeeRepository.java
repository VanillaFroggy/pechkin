package ru.intech.pechkin.corporate.infrastructure.persistence.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import ru.intech.pechkin.corporate.infrastructure.persistence.entity.Employee;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, UUID> {
    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByPhoneNumber(String phoneNumber);

    List<Employee> findAllByDepartment(UUID department);

    List<Employee> findAllByDepartmentIn(List<UUID> departments);

    @Query(value = "{ 'fio' : { $regex: ?1, $options: 'i' } }", count = true)
    List<Employee> findByFioLikeIgnoreCase(String fio);

    @Query(value = "{ 'position' : { $regex: ?1, $options: 'i' } }", count = true)
    List<Employee> findByPositionLikeIgnoreCase(String fio);
}
