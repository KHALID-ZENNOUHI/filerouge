package org.dev.filerouge.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dev.filerouge.domain.Enum.Role;
import org.dev.filerouge.domain.Student;
import org.dev.filerouge.domain.User;
import org.dev.filerouge.service.implementation.EmailService;
import org.dev.filerouge.web.error.ServiceException;
import org.dev.filerouge.repository.StudentRepository;
import org.dev.filerouge.repository.UserRepository;
import org.dev.filerouge.web.vm.user.PasswordUpdateDTO;
import org.dev.filerouge.web.vm.user.UserDTO;
import org.dev.filerouge.web.vm.user.UserRoleCountDTO;
import org.dev.filerouge.web.vm.user.UserStatisticsDTO;
import org.dev.filerouge.web.vm.user.UserStatusUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing users
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserManagementService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    /**
     * Get all users with pagination
     *
     * @param pageable the pagination information
     * @return a page of users
     */
    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::convertToDTO);
    }

    /**
     * Get user by ID
     *
     * @param id the ID of the user
     * @return the user
     */
    @Transactional(readOnly = true)
    public UserDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("User", "id", id));
        return convertToDTO(user);
    }

    /**
     * Update user status
     *
     * @param id the ID of the user
     * @param statusUpdate the status update
     * @return the updated user
     */
    @Transactional
    public UserDTO updateUserStatus(UUID id, UserStatusUpdateDTO statusUpdate) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("User", "id", id));

        user.setEnabled(statusUpdate.getEnabled());
        user.setLocked(statusUpdate.getLocked());

        // Notify user of account status change
        sendAccountStatusChangeEmail(user, statusUpdate);

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    /**
     * Delete a user
     *
     * @param id the ID of the user
     */
    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("User", "id", id));

        // Check if the user is not the last administrator
        if (user.getRole() == Role.ADMINISTRATOR && userRepository.countByRole(Role.ADMINISTRATOR.name()) <= 1) {
            throw new ServiceException.OperationNotAllowedException("Cannot delete the last administrator");
        }

        // Delete the user
        userRepository.delete(user);

        // Send deletion notification
        sendAccountDeletionEmail(user);
    }

    /**
     * Search users by name
     *
     * @param query the search query
     * @param pageable the pagination information
     * @return a page of users
     */
    @Transactional(readOnly = true)
    public Page<UserDTO> searchUsersByName(String query, Pageable pageable) {
        return userRepository.searchByName(query, pageable).map(this::convertToDTO);
    }

    /**
     * Get users by role
     *
     * @param role the role
     * @param pageable the pagination information
     * @return a page of users
     */
    @Transactional(readOnly = true)
    public Page<UserDTO> getUsersByRole(String role, Pageable pageable) {
        return userRepository.findByRole(role, pageable).map(this::convertToDTO);
    }

    /**
     * Reset a user's password with admin-provided password
     *
     * @param id the ID of the user
     * @param passwordUpdate the password update DTO containing the new password
     * @return success message
     */
    @Transactional
    public String resetUserPassword(UUID id, PasswordUpdateDTO passwordUpdate) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("User", "id", id));

        // Update user's password with the provided password
        user.setPassword(passwordEncoder.encode(passwordUpdate.getNewPassword()));
        userRepository.save(user);

        // Send password reset email
        sendPasswordResetEmail(user, passwordUpdate.getNewPassword());

        return "Password has been successfully reset";
    }

    /**
     * Get user statistics
     *
     * @return user statistics
     */
    @Transactional(readOnly = true)
    public UserStatisticsDTO getUserStatistics() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countActiveUsers();
        long lockedUsers = userRepository.countByLocked(true);
        long disabledUsers = userRepository.countByLocked(false) - activeUsers;

        // Count users by role
        Map<String, Long> usersByRole = Arrays.stream(Role.values())
                .map(role -> new UserRoleCountDTO(role, userRepository.countByRole(role.name())))
                .collect(Collectors.toMap(
                        dto -> dto.getRole().name(),
                        UserRoleCountDTO::getCount
                ));





        // Count users who never logged in
        long neverLoggedInUsers = userRepository.findByLastLoginIsNull(Pageable.unpaged()).getTotalElements();

        return UserStatisticsDTO.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .lockedUsers(lockedUsers)
                .disabledUsers(disabledUsers)
                .usersByRole(usersByRole)
                .neverLoggedInUsers(neverLoggedInUsers)
                .build();
    }

    /**
     * Convert a User entity to a UserDTO
     *
     * @param user the user entity
     * @return the user DTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = UserDTO.builder()
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
                .role(user.getRole())
                .enabled(user.isEnabled())
                .locked(user.isLocked())
                .lastLogin(user.getLastLogin())
                .build();

        // Add fields specific to Student
        if (user instanceof Student) {
            Student student = (Student) user;
            if (student.getStudentClass() != null) {
                dto.setClassId(student.getStudentClass().getId());
                dto.setClassName(student.getStudentClass().getName());
            }

            if (student.getParent() != null) {
                dto.setParentId(student.getParent().getId());
                dto.setParentName(student.getParent().getFirstName() + " " + student.getParent().getLastName());
            }
        }

        return dto;
    }

    /**
     * Send password reset email
     *
     * @param user the user
     * @param newPassword the new password
     */
    private void sendPasswordResetEmail(User user, String newPassword) {
        String subject = "Your Password Has Been Reset";
        String content = String.format(
                "Dear %s %s,\n\n" +
                        "Your password has been reset by an administrator.\n\n" +
                        "Your new password is: %s\n\n" +
                        "Please log in with this password and change it immediately.\n\n" +
                        "Best regards,\n" +
                        "School Management Team",
                user.getFirstName(),
                user.getLastName(),
                newPassword
        );

        emailService.sendEmail(user.getEmail(), subject, content);
    }

    /**
     * Send account status change email
     *
     * @param user the user
     * @param statusUpdate the status update
     */
    private void sendAccountStatusChangeEmail(User user, UserStatusUpdateDTO statusUpdate) {
        String statusMessage;
        if (!statusUpdate.getEnabled()) {
            statusMessage = "disabled";
        } else if (statusUpdate.getLocked()) {
            statusMessage = "locked";
        } else {
            statusMessage = "updated";
        }

        String subject = "Your Account Status Has Changed";
        String content = String.format(
                "Dear %s %s,\n\n" +
                        "Your account has been %s by an administrator.\n\n" +
                        "Reason: %s\n\n" +
                        "If you have any questions, please contact the administrator.\n\n" +
                        "Best regards,\n" +
                        "School Management Team",
                user.getFirstName(),
                user.getLastName(),
                statusMessage,
                statusUpdate.getReasonForChange()
        );

        emailService.sendEmail(user.getEmail(), subject, content);
    }

    /**
     * Send account deletion email
     *
     * @param user the user
     */
    private void sendAccountDeletionEmail(User user) {
        String subject = "Your Account Has Been Deleted";
        String content = String.format(
                "Dear %s %s,\n\n" +
                        "Your account has been deleted by an administrator.\n\n" +
                        "If you believe this is an error, please contact the administrator.\n\n" +
                        "Best regards,\n" +
                        "School Management Team",
                user.getFirstName(),
                user.getLastName()
        );

        emailService.sendEmail(user.getEmail(), subject, content);
    }
}