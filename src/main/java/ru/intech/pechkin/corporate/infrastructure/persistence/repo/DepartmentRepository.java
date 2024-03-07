package ru.intech.pechkin.corporate.infrastructure.persistence.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import ru.intech.pechkin.corporate.infrastructure.persistence.entity.Department;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DepartmentRepository extends MongoRepository<Department, UUID> {
    Optional<Department> findByTitle(String title);

    @Query(value = "{ 'title' : { $regex: ?0, $options: 'i' } }", count = true)
    List<Department> findByTitleLikeIgnoreCase(String  title);
}
