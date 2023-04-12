package ru.intech.pechkin.auth.repo;

import ru.intech.pechkin.auth.model.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttributeRepository extends JpaRepository<Attribute, Long> {
    Optional<Attribute> findByValue(String value);
}
