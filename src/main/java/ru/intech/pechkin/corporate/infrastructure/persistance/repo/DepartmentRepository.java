package ru.intech.pechkin.corporate.infrastructure.persistance.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.intech.pechkin.corporate.infrastructure.persistance.entity.Department;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DepartmentRepository extends MongoRepository<Department, UUID> {
    Optional<Department> findByTitle(String title);
}
