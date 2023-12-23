package ru.intech.pechkin.messenger.infrastructure.persistance.entity;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Document("users")
public class User implements UserDetails {
    @Id
    private UUID id;

    @Pattern(regexp = "^\\w{4,32}$")
    private String username;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).{12,50}$")
    private String password;

    private String fio;

    @Pattern(regexp = "^(\\+7|8)[(]?\\d{3}[)]?[-\\s\\\\.]?\\d{3}[-\\s.]?\\d{4}$")
    private String phoneNumber;

    @Pattern(regexp = "^[\\w.%+-]+@[a-z\\d.-]+\\\\.[a-z]{2,6}$")
    private String email;

    @Pattern(regexp = "^\\w{4,32}$")
    private String department;

    @Pattern(regexp = "^\\w{4,32}$")
    private String position;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("USER"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
