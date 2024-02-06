package ru.intech.pechkin.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.User;
import ru.intech.pechkin.messenger.infrastructure.persistance.repo.UserRepository;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MinioClient minioClient;

    @Value("${spring.minio.bucket}")
    private String bucket;

    @Override
    @SneakyThrows
    public void run(ApplicationArguments args) {
        if (userRepository.count() == 0) {
            User user = User.builder()
                    .id(UUID.fromString("d78ab7a3-6725-43c8-8012-35d826caf988"))
                    .username("admin")
                    .password(passwordEncoder.encode("Nerds4ever<3"))
                    .fio("Админ Админович")
                    .blocked(false)
                    .build();
            userRepository.save(user);
        }
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucket)
                            .build()
            );
        }
    }
}