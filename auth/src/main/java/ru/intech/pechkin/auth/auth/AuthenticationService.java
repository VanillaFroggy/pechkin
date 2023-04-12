package ru.intech.pechkin.auth.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.intech.pechkin.auth.config.JwtService;
import ru.intech.pechkin.auth.model.Attribute;
import ru.intech.pechkin.auth.model.Group;
import ru.intech.pechkin.auth.model.User;
import ru.intech.pechkin.auth.repo.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final GroupTypeRepository groupTypeRepository;
    private final AttributeTypeRepository attributeTypeRepository;
    private final AttributeRepository attributeRepository;
    private final GroupRepository groupRepository;
    private final List<Attribute> registerAttributes = new ArrayList<>();
    private final List<Group> registerGroups = new ArrayList<>();
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким именем уже существует");
        } else if (attributeRepository.findByValue(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Этот email уже зарегестрирован");
        } else if (attributeRepository.findByValue(request.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("Этот номер телефона уже зарегестрирован");
        } else if (!request.getUsername().matches(User.USERNAME_VALIDATION)) {
            throw new IllegalArgumentException("Имя пользователя должно состоять из латинских букв, цифр и знака _," +
                    " также оно должно содержать не меньше 4-х и не более 32-х знаков");
        }

        final Date END_DATE = Date.from(LocalDate.now().plusMonths(6).atStartOfDay(ZoneId.systemDefault()).toInstant());

        registerAttributes.add(
                getBuiltAttribute(
                        "password",
                        passwordEncoder.encode(checkValidation("password", request.getPassword())),
                        END_DATE
                )
        );
        registerAttributes.add(
                getBuiltAttribute(
                        "email",
                        checkValidation("email", request.getEmail()),
                        END_DATE
                )
        );
        registerAttributes.add(
                getBuiltAttribute(
                        "phonenumber",
                        checkValidation("phonenumber", request.getPhoneNumber()),
                        END_DATE
                )
        );
        registerGroups.add(
                Group.builder()
                        .groupType(groupTypeRepository.findByName("user").orElse(null))
                        .build()
        );

        var user = User.builder()
                .username(request.getUsername())
                .attributes(registerAttributes)
                .groups(registerGroups)
                .begDate(new Date(System.currentTimeMillis()))
                .endDate(END_DATE)
                .build();
        userRepository.save(user);

        registerAttributes.forEach(attribute -> attribute.setUser(user));
        attributeRepository.saveAll(registerAttributes);

        registerGroups.forEach(group -> group.setUser(user));
        groupRepository.saveAll(registerGroups);

        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    private String checkValidation(String attributeName, String attributeValue) {
        var attributeType = attributeTypeRepository.findByName(attributeName).orElse(null);
        boolean isValid = attributeValue.matches(Objects.requireNonNull(attributeType).getValidation());
        if (!isValid) throw new IllegalArgumentException(attributeType.getValidationDescription());
        return attributeValue;
    }

    private Attribute getBuiltAttribute(String attributeName, String validValue, Date endDate) {
        return Attribute.builder()
                .attributeType(attributeTypeRepository.findByName(attributeName).orElse(null))
                .value(validValue)
                .begDate(new Date(System.currentTimeMillis()))
                .endDate(endDate)
                .isValid(validValue.equals("password")) // other types aren't valid yet, but password
                .isMainAttr(true)
                .build();
    }
}
