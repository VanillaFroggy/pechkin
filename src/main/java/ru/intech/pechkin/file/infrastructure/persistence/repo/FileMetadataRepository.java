package ru.intech.pechkin.file.infrastructure.persistence.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.intech.pechkin.file.infrastructure.persistence.entity.FileMetadata;

import java.util.Optional;

@Repository
public interface FileMetadataRepository extends MongoRepository<FileMetadata, String> {
    Optional<FileMetadata> findByHash(String hash);
}
