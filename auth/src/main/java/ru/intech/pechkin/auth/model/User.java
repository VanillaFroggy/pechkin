package ru.intech.pechkin.auth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "usr")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;

    public static final String USERNAME_VALIDATION = "^\\w{4,32}$";

    @CreatedDate
    @Column(name = "beg_date")
    private Date begDate;

    @Column(name = "end_date")
    private Date endDate;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
//    @JoinColumn(name = "user_id", table = "grp");
    private List<Group> groups;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
//    @JoinColumn(name = "user_id", table = "attr")
    private List<Attribute> attributes;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return mapToGrantedAuthorities(groups);
    }

    @Override
    public String getPassword() {
        Attribute password = findAttributeById(1L);
        return password != null ? password.getValue() : null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return System.currentTimeMillis() < endDate.getTime();
    }

    @Override
    public boolean isAccountNonLocked() {
        return groups.stream()
                .noneMatch(group -> group.getGroupType().getId().equals(3L));
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return attributes.stream()
                .noneMatch(attribute -> attribute.getEndDate().getTime() < System.currentTimeMillis() && attribute.isMainAttr());
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    private Attribute findAttributeById(Long id) {
        return attributes.stream()
                .filter(attribute ->
                        attribute.getAttributeType().getId().equals(id) && attribute.isMainAttr() && attribute.isValid())
                .findFirst().orElse(null);
    }

    private List<GrantedAuthority> mapToGrantedAuthorities(List<Group> groups) {
        return groups.stream()
                .map(group -> new SimpleGrantedAuthority(group.getGroupType().getName()))
                .collect(Collectors.toList());
    }
}
