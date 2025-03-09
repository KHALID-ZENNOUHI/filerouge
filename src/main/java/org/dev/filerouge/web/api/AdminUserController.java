package org.dev.filerouge.web.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dev.filerouge.domain.User;
import org.dev.filerouge.domain.Enum.Role;
import org.dev.filerouge.service.implementation.AuthService;
import org.dev.filerouge.service.UserManagementService;
import org.dev.filerouge.web.vm.auth.RegisterUserRequest;
import org.dev.filerouge.web.vm.user.PasswordUpdateDTO;
import org.dev.filerouge.web.vm.user.UserDTO;
import org.dev.filerouge.web.vm.user.UserStatisticsDTO;
import org.dev.filerouge.web.vm.user.UserStatusUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for admin user management operations
 */
@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMINISTRATOR')")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {

    private final UserManagementService userManagementService;
    private final AuthService authService;

    /**
     * Get all users with pagination
     *
     * @param pageable the pagination information
     * @return a page of users
     */
    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAllUsers(Pageable pageable) {
        log.debug("REST request to get a page of users");
        Page<UserDTO> page = userManagementService.getAllUsers(pageable);
        return ResponseEntity.ok().body(page);
    }

    /**
     * Get user by ID
     *
     * @param id the ID of the user to retrieve
     * @return the user
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        log.debug("REST request to get user with ID: {}", id);
        UserDTO userDTO = userManagementService.getUserById(id);
        return ResponseEntity.ok().body(userDTO);
    }

    /**
     * Create a new user
     *
     * @param registerRequest the user to create
     * @return the created user
     */
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody RegisterUserRequest registerRequest) {
        log.debug("REST request to create user: {}", registerRequest.getUsername());
        User newUser = authService.registerUser(registerRequest, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    /**
     * Update user status (enable/disable, lock/unlock)
     *
     * @param id the ID of the user to update
     * @param statusUpdate the status update
     * @return the updated user
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<UserDTO> updateUserStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UserStatusUpdateDTO statusUpdate) {
        log.debug("REST request to update status of user with ID: {}", id);
        UserDTO updatedUser = userManagementService.updateUserStatus(id, statusUpdate);
        return ResponseEntity.ok().body(updatedUser);
    }

    /**
     * Delete a user
     *
     * @param id the ID of the user to delete
     * @return no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        log.debug("REST request to delete user with ID: {}", id);
        userManagementService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Search users by name
     *
     * @param query the search query
     * @param pageable the pagination information
     * @return a page of users
     */
    @GetMapping("/search")
    public ResponseEntity<Page<UserDTO>> searchUsers(
            @RequestParam String query,
            Pageable pageable) {
        log.debug("REST request to search users by name: {}", query);
        Page<UserDTO> page = userManagementService.searchUsersByName(query, pageable);
        return ResponseEntity.ok().body(page);
    }

    /**
     * Get users by role
     *
     * @param role the role
     * @param pageable the pagination information
     * @return a page of users
     */
    @GetMapping("/by-role/{role}")
    public ResponseEntity<Page<UserDTO>> getUsersByRole(
            @PathVariable Role role,
            Pageable pageable) {
        log.debug("REST request to get users by role: {}", role);
        Page<UserDTO> page = userManagementService.getUsersByRole(role.name(), pageable);
        return ResponseEntity.ok().body(page);
    }

    /**
     * Reset a user's password
     *
     * @param id the ID of the user
     * @param passwordUpdate the new password details
     * @return success message
     */
    @PostMapping("/{id}/reset-password")
    public ResponseEntity<String> resetUserPassword(
            @PathVariable UUID id,
            @Valid @RequestBody PasswordUpdateDTO passwordUpdate) {
        log.debug("REST request to reset password for user with ID: {}", id);

        // Validate that new password and confirm password match
        if (!passwordUpdate.getNewPassword().equals(passwordUpdate.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("New password and confirm password do not match");
        }

        String result = userManagementService.resetUserPassword(id, passwordUpdate);
        return ResponseEntity.ok().body(result);
    }

    /**
     * Get user statistics
     *
     * @return user statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<UserStatisticsDTO> getUserStatistics() {
        log.debug("REST request to get user statistics");
        UserStatisticsDTO statistics = userManagementService.getUserStatistics();
        return ResponseEntity.ok().body(statistics);
    }
}