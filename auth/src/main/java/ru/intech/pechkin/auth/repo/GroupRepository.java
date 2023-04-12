package ru.intech.pechkin.auth.repo;

import ru.intech.pechkin.auth.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository  extends JpaRepository<Group, Long> {
}
