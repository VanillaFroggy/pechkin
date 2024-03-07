package ru.intech.pechkin.messenger.infrastructure.persistence.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends MongoRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    @Query(value = "{ 'username' : { $regex: ?1, $options: 'i' } }", count = true)
    List<User> findAllByUsernameLikeIgnoreCase(String username);

    Optional<User> findByEmployeeId(UUID employeeId);
}
