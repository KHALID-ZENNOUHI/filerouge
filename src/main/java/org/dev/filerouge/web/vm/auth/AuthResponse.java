package org.dev.filerouge.web.vm.auth;

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

/**
 * Response DTO for successful authentication
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String refreshToken;
    private String username;
    private String fullName;
    private Role role;

}