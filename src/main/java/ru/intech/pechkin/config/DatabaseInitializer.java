package ru.intech.pechkin.config;

import lombok.RequiredArgsConstructor;
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

    @Override
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
    }
}