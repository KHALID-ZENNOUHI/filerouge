package org.dev.filerouge.web.vm.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dev.filerouge.domain.Enum.Gender;
import org.dev.filerouge.domain.Enum.Role;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request DTO for user registration by admin
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter")
    @Pattern(regexp = ".*[a-z].*", message = "Password must contain at least one lowercase letter")
    @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit")
    private String password;

    @NotBlank(message = "CIN is required")
    @Pattern(regexp = "^[A-Za-z]{2}\\d{6}$", message = "CIN must start with 2 letters followed by 6 digits (e.g., AB123456)")
    private String cin;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(06|07|05)\\d{8}$", message = "Phone number must start with 06, 07, or 05 followed by 8 digits")
    private String phone;

    private LocalDate birthDate;

    private String birthPlace;

    private String address;

    private Gender gender;

    private String photo;

    private Role role;

    // For student registration
    private UUID classId;

    // For student registration
    private UUID parentId;
}