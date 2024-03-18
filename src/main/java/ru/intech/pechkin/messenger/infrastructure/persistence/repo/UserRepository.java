package ru.intech.pechkin.messenger.infrastructure.persistence.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.User;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends MongoRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    @Query(value = "{ 'username' : { $regex: ?0, $options: 'i' } }", count = true)
    Page<User> findAllByUsernameLikeIgnoreCase(String username, Pageable pageable);

    Optional<User> findByEmployeeId(UUID employeeId);

    Page<User> findAllByEmployeeIdIn(Collection<UUID> employeeId, Pageable pageable);
}
