package org.dev.filerouge.domain;

import jakarta.validation.constraints.*;
import lombok.*;
import jakarta.persistence.*;
import org.dev.filerouge.domain.Enum.Gender;
import org.dev.filerouge.domain.Enum.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id",updatable = false)
    private UUID id;

    @Column(unique = true)
    @Size(min = 8, max = 20, message = "The username must be at least 8 characters long.")
    private String username;

    @Size(min = 2, max = 20, message = "The first name must be at least 2 characters long.")
    private String firstName;

    @Size(min = 2, max = 20, message = "The last name must be at least 2 characters long.")
    private String lastName;

    @Email
    @NotBlank
    private String email;

    @Size(min = 8, message = "The password must be at least 8 characters long.")
    @Pattern(regexp = ".*[A-Z].*", message = "The password must contain at least one uppercase letter.")
    @Pattern(regexp = ".*[a-z].*", message = "The password must contain at least one lowercase letter.")
    @Pattern(regexp = ".*\\d.*", message = "The password must contain at least one digit.")
    private String password;

    @Column(unique = true)
    @Pattern(regexp = "^[A-Za-z]{2}\\d{6}$", message = "CIN must start with 2 letters followed by 6 digits (e.g., AB123456).")
    private String cin;

    @Pattern(regexp = "^(06|07|05)\\d{8}$", message = "Phone number must start with 06, 07, or 05 followed by 8 digits (e.g., 0612345678).")
    private String phone;

    @Past
    private LocalDate birthDate;

    @NotBlank
    private String birthPlace;

    @NotBlank
    private String address;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String photo;

    @Column(insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = role.getPermissions().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        authorities.add(new SimpleGrantedAuthority("ROLE_"+role.name()));
        return authorities;
    }
}

