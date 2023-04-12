package ru.intech.pechkin.auth.repo;

import ru.intech.pechkin.auth.model.GroupType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupTypeRepository extends JpaRepository<GroupType, Long> {
    Optional<GroupType> findByName(String name);
}
