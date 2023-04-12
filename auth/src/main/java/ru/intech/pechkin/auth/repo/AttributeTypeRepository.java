package ru.intech.pechkin.auth.repo;

import ru.intech.pechkin.auth.model.AttributeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttributeTypeRepository extends JpaRepository<AttributeType, Long> {
    Optional<AttributeType> findByName(String name);
}
