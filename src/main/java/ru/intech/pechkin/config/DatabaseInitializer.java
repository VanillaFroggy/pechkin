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
import ru.intech.pechkin.corporate.infrastructure.persistence.entity.Employee;
import ru.intech.pechkin.corporate.infrastructure.persistence.repo.EmployeeRepository;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.User;
import ru.intech.pechkin.messenger.infrastructure.persistence.repo.UserRepository;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements ApplicationRunner {
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MinioClient minioClient;

    @Value("${spring.minio.bucket}")
    private String bucket;

    @Override
    @SneakyThrows
    public void run(ApplicationArguments args) {
        if (userRepository.count() == 0) {
            createFirstUser();
        }
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucket)
                            .build()
            );
        }
    }

    private void createFirstUser() {
        Employee employee = Employee.builder()
                .id(UUID.fromString("ff28c40e-73cb-4cc6-936a-501adad617f5"))
                .fio("Админ Админович")
                .build();
        employeeRepository.save(employee);
        userRepository.save(
                User.builder()
                        .id(UUID.fromString("d78ab7a3-6725-43c8-8012-35d826caf988"))
                        .employeeId(employee.getId())
                        .username("admin")
                        .password(passwordEncoder.encode("Nerds4ever<3"))
                        .blocked(false)
                        .build()
        );
    }
}