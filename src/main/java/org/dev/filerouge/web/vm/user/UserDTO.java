package org.dev.filerouge.web.vm.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dev.filerouge.domain.*;
import org.dev.filerouge.domain.Enum.Gender;
import org.dev.filerouge.domain.Enum.Role;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for User entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String cin;
    private String phone;
    private LocalDate birthDate;
    private String birthPlace;
    private String address;
    private Gender gender;
    private String photo;
    private Role role;
    public static UserDTO fromUser(User user) {
        UserDTO response = new UserDTO();
        // Set all standard fields

        // Determine effective role
        if (user instanceof Administrator) {
            response.setRole(Role.ADMINISTRATOR);
        } else if (user instanceof Teacher) {
            response.setRole(Role.TEACHER);
        } else if (user instanceof Student) {
            response.setRole(Role.STUDENT);
        } else if (user instanceof Parent) {
            response.setRole(Role.PARENT);
        } else {
            response.setRole(user.getRole());
        }

        return response;
    }
    private boolean enabled;
    private boolean locked;
    private LocalDate lastLogin;
    private LocalDate createdDate;
    private boolean twoFactorEnabled;

    // Fields specific to different user types
    private UUID classId;
    private String className;
    private UUID parentId;
    private String parentName;
}