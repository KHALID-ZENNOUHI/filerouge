package org.dev.filerouge.web.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dev.filerouge.domain.User;
import org.dev.filerouge.service.implementation.AuthService;
import org.dev.filerouge.web.vm.auth.*;
import org.dev.filerouge.web.vm.mapper.UserMapper;
import org.dev.filerouge.web.vm.user.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication and user registration
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;

    /**
     * Authenticate a user and return a JWT token
     *
     * @param loginRequest the login request
     * @param request the HTTP request
     * @return the authentication response
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        log.debug("REST request to login user: {}", loginRequest.getUsername());
        AuthResponse response = authService.login(loginRequest, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Register a new user (admin only)
     *
     * @param registerRequest the registration request
     * @param request the HTTP request
     * @return the created user
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody RegisterUserRequest registerRequest, HttpServletRequest request) {
        try {
            log.debug("REST request to register user: {}", registerRequest.getUsername());
            log.debug("Register request details: {}", registerRequest);
            User user = authService.registerUser(registerRequest, request);
            UserDTO userDTO = userMapper.userToUserDTO(user);
            log.debug("User registered successfully: {}", user.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
        } catch (Exception e) {
            log.error("Error registering user: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Request a password reset
     *
     * @param resetRequest the password reset request
     * @return the password reset response
     */
    @PostMapping("/reset-password/request")
    public ResponseEntity<PasswordResetResponse> requestPasswordReset(@Valid @RequestBody PasswordResetRequest resetRequest) {
        log.debug("REST request to reset password for email: {}", resetRequest.getEmail());
        PasswordResetResponse response = authService.requestPasswordReset(resetRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Confirm password reset
     *
     * @param confirmRequest the password reset confirmation request
     * @return the password reset response
     */
    @PostMapping("/reset-password/confirm")
    public ResponseEntity<PasswordResetResponse> confirmPasswordReset(@Valid @RequestBody PasswordResetConfirmRequest confirmRequest) {
        log.debug("REST request to confirm password reset");
        PasswordResetResponse response = authService.confirmPasswordReset(confirmRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Change password for authenticated user
     *
     * @param changeRequest the change password request
     * @return the password reset response
     */
    @PostMapping("/change-password")
    public ResponseEntity<PasswordResetResponse> changePassword(@Valid @RequestBody ChangePasswordRequest changeRequest) {
        log.debug("REST request to change password for authenticated user");
        PasswordResetResponse response = authService.changePassword(changeRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Check if user is authenticated and token is valid
     *
     * @return HTTP 200 OK status if authenticated
     */
    @GetMapping("/check")
    public ResponseEntity<Void> checkAuthentication() {
        return ResponseEntity.ok().build();
    }
}