package org.dev.filerouge.web.vm.mapper;

import org.dev.filerouge.domain.*;
import org.dev.filerouge.domain.Enum.Role;
import org.dev.filerouge.web.vm.LoginVM;
import org.dev.filerouge.web.vm.ProfileVM;
import org.dev.filerouge.web.vm.RegisterVM;
import org.dev.filerouge.web.vm.user.UserDTO;
import org.springframework.stereotype.Component;

/**
 * Manual implementation of UserMapper
 */
@Component
public class UserMapper {

    /**
     * Convert User entity to UserDTO
     */
    public UserDTO userToUserDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO.UserDTOBuilder builder = UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .cin(user.getCin())
                .phone(user.getPhone())
                .birthDate(user.getBirthDate())
                .birthPlace(user.getBirthPlace())
                .address(user.getAddress())
                .gender(user.getGender())
                .photo(user.getPhoto())
                .enabled(user.isEnabled())
                .locked(user.isLocked())
                .lastLogin(user.getLastLogin());

        // Set role based on user type
        if (user instanceof Administrator) {
            builder.role(Role.ADMINISTRATOR);
        } else if (user instanceof Teacher) {
            builder.role(Role.TEACHER);
        } else if (user instanceof Student) {
            builder.role(Role.STUDENT);

            // Set student-specific fields
            Student student = (Student) user;
            if (student.getStudentClass() != null) {
                builder.classId(student.getStudentClass().getId());
                builder.className(student.getStudentClass().getName());
            }

            if (student.getParent() != null) {
                builder.parentId(student.getParent().getId());
                builder.parentName(student.getParent().getFirstName() + " " + student.getParent().getLastName());
            }
        } else if (user instanceof Parent) {
            builder.role(Role.PARENT);
        } else {
            builder.role(user.getRole());
        }

        return builder.build();
    }

    // Add other mapping methods as needed for LoginVM, ProfileVM, RegisterVM, etc.
}