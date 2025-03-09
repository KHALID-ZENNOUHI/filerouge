package org.dev.filerouge.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.dev.filerouge.domain.Enum.Gender;
import org.dev.filerouge.domain.Enum.Role;

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
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false)
    private UUID id;

    @Column(unique = true)
    @Size(min = 3, max = 50, message = "The username must be between 3 and 50 characters")
    private String username;

    @Size(min = 2, max = 50, message = "The first name must be between 2 and 50 characters")
    private String firstName;

    @Size(min = 2, max = 50, message = "The last name must be between 2 and 50 characters")
    private String lastName;

    @Email
    @NotBlank
    private String email;

    @Size(min = 8, message = "The password must be at least 8 characters long")
    @Pattern(regexp = ".*[A-Z].*", message = "The password must contain at least one uppercase letter")
    @Pattern(regexp = ".*[a-z].*", message = "The password must contain at least one lowercase letter")
    @Pattern(regexp = ".*\\d.*", message = "The password must contain at least one digit")
    private String password;

    @Column(unique = true)
    @Pattern(regexp = "^[A-Za-z]{2}\\d{6}$", message = "CIN must start with 2 letters followed by 6 digits (e.g., AB123456)")
    private String cin;

    @Pattern(regexp = "^(06|07|05)\\d{8}$", message = "Phone number must start with 06, 07, or 05 followed by 8 digits (e.g., 0612345678)")
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

    // Added fields for account status
    private boolean enabled = true;
    private boolean locked = false;

    // Password reset fields
    private String resetToken;
    private LocalDate resetTokenExpiry;

    // Last login time and IP
    private LocalDate lastLogin;
    private String lastLoginIp;
}