package org.dev.filerouge.service.implementation;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dev.filerouge.domain.*;
import org.dev.filerouge.domain.Class;
import org.dev.filerouge.domain.Enum.Gender;
import org.dev.filerouge.domain.Enum.Role;
import org.dev.filerouge.web.error.ServiceException;
import org.dev.filerouge.repository.*;
import org.dev.filerouge.security.JwtTokenProvider;
import org.dev.filerouge.web.vm.auth.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for authentication and user management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;
    private final AdministratorRepository administratorRepository;
    private final ClassRepository classRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    /**
     * Authenticate a user and return a JWT token
     *
     * @param loginRequest the login request
     * @param request the HTTP request
     * @return the authentication response
     */
    @Transactional
    public AuthResponse login(LoginRequest loginRequest, HttpServletRequest request) {
        try {
            log.debug("Attempting to authenticate user: {}", loginRequest.getUsername());

            // Check if user exists first
            boolean userExists = userRepository.existsByUsername(loginRequest.getUsername());
            log.debug("User exists in database: {}", userExists);

            if (!userExists) {
                log.warn("User does not exist: {}", loginRequest.getUsername());
                throw new ServiceException.ValidationException("Invalid username or password");
            }

            // Find the user and verify password
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new ServiceException.ResourceNotFoundException("User", "username", loginRequest.getUsername()));

            log.debug("Found user: {}. Role from entity: {}", user.getUsername(), user.getRole());
            log.debug("User entity type: {}", user.getClass().getSimpleName());
            log.debug("User password hash: {}", user.getPassword().substring(0, 10) + "...");

            boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
            log.debug("Password matches: {}", passwordMatches);

            if (!passwordMatches) {
                log.warn("Password does not match for user: {}", loginRequest.getUsername());
                throw new ServiceException.ValidationException("Invalid username or password");
            }

            // At this point, we know the user exists and password is correct
            // Instead of using authenticationManager, we'll create the authentication directly

            try {
                // Determine effective role
                Role effectiveRole = determineEffectiveRole(user);
                log.debug("Determined effective role: {}", effectiveRole);

                // Create authorities
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();

                // Add the role itself as an authority
                String roleAuthority = "ROLE_" + effectiveRole.name();
                log.debug("Adding role authority: {}", roleAuthority);
                authorities.add(new SimpleGrantedAuthority(roleAuthority));

                // Add permissions if available
                if (effectiveRole.getPermissions() != null) {
                    log.debug("Adding permission authorities: {}", effectiveRole.getPermissions());
                    authorities.addAll(effectiveRole.getPermissions().stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList()));
                } else {
                    log.warn("Permissions are null for role: {}", effectiveRole);
                }

                // Create authentication token
                log.debug("Creating authentication token");
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        null, // Credentials are cleared after authentication
                        authorities
                );

                // Set authentication in security context
                log.debug("Setting authentication in security context");
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Generate tokens
                log.debug("Generating tokens");
                String jwt = tokenProvider.createToken(authentication);
                String refreshToken = tokenProvider.createRefreshToken(authentication);

                // Update user's last login
                log.debug("Updating last login information");
                user.setLastLogin(LocalDate.now());
                user.setLastLoginIp(request.getRemoteAddr());
                userRepository.save(user);

                // Return the response
                log.debug("Building and returning response");
                return AuthResponse.builder()
                        .token(jwt)
                        .refreshToken(refreshToken)
                        .username(user.getUsername())
                        .fullName(user.getFirstName() + " " + user.getLastName())
                        .role(effectiveRole)
                        .build();
            } catch (Exception e) {
                log.error("Error during manual authentication for user: {}", loginRequest.getUsername(), e);
                throw new ServiceException("Error during authentication: " + e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR, "AUTHENTICATION_ERROR");
            }
        } catch (ServiceException ex) {
            throw ex; // Re-throw service exceptions
        } catch (Exception e) {
            log.error("Unexpected error during login for user: {}", loginRequest.getUsername(), e);
            throw new ServiceException("Error during login: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");
        }
    }


    /**
     * Register a new user (admin only)
     *
     * @param registerRequest the registration request
     * @param request the HTTP request
     * @return the created user
     */
    @Transactional
    public User registerUser(RegisterUserRequest registerRequest, HttpServletRequest request) {
        log.debug("Registering new user: {}", registerRequest.getUsername());

        // Check if username already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new ServiceException.DuplicateResourceException("User", "username", registerRequest.getUsername());
        }

        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ServiceException.DuplicateResourceException("User", "email", registerRequest.getEmail());
        }

        // Check if CIN already exists
        if (userRepository.existsByCin(registerRequest.getCin())) {
            throw new ServiceException.DuplicateResourceException("User", "CIN", registerRequest.getCin());
        }

        // Create user based on role
        User user;

        switch (registerRequest.getRole()) {
            case ADMINISTRATOR:
                user = createAdministrator(registerRequest);
                break;
            case TEACHER:
                user = createTeacher(registerRequest);
                break;
            case STUDENT:
                user = createStudent(registerRequest);
                break;
            case PARENT:
                user = createParent(registerRequest);
                break;
            default:
                throw new ServiceException.ValidationException("Invalid role: " + registerRequest.getRole());
        }

        // Send welcome email with credentials
//        sendWelcomeEmail(user, registerRequest.getPassword());

        return user;
    }

    /**
     * Create an Administrator user
     *
     * @param registerRequest the registration request
     * @return the created Administrator
     */
    private Administrator createAdministrator(RegisterUserRequest registerRequest) {
        Administrator administrator = new Administrator();
        setCommonUserFields(administrator, registerRequest);

        return administratorRepository.save(administrator);
    }

    /**
     * Create a Teacher user
     *
     * @param registerRequest the registration request
     * @return the created Teacher
     */
    private Teacher createTeacher(RegisterUserRequest registerRequest) {
        Teacher teacher = new Teacher();
        setCommonUserFields(teacher, registerRequest);

        return teacherRepository.save(teacher);
    }

    /**
     * Create a Student user
     *
     * @param registerRequest the registration request
     * @return the created Student
     */
    private Student createStudent(RegisterUserRequest registerRequest) {
        Student student = new Student();
        setCommonUserFields(student, registerRequest);

        // Set student-specific fields
        if (registerRequest.getClassId() != null) {
            Class studentClass = classRepository.findById(registerRequest.getClassId())
                    .orElseThrow(() -> new ServiceException.ResourceNotFoundException("Class", "id", registerRequest.getClassId()));
            student.setStudentClass(studentClass);
        }

        if (registerRequest.getParentId() != null) {
            Parent parent = parentRepository.findById(registerRequest.getParentId())
                    .orElseThrow(() -> new ServiceException.ResourceNotFoundException("Parent", "id", registerRequest.getParentId()));
            student.setParent(parent);
        }

        return studentRepository.save(student);
    }

    /**
     * Create a Parent user
     *
     * @param registerRequest the registration request
     * @return the created Parent
     */
    private Parent createParent(RegisterUserRequest registerRequest) {
        Parent parent = new Parent();
        setCommonUserFields(parent, registerRequest);

        return parentRepository.save(parent);
    }

    /**
     * Set common fields for all user types
     *
     * @param user the user to update
     * @param registerRequest the registration request
     */
    private void setCommonUserFields(User user, RegisterUserRequest registerRequest) {
        user.setUsername(registerRequest.getUsername());
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCin(registerRequest.getCin());
        user.setPhone(registerRequest.getPhone());
        user.setBirthDate(registerRequest.getBirthDate());
        user.setBirthPlace(registerRequest.getBirthPlace());
        user.setAddress(registerRequest.getAddress());
        user.setGender(registerRequest.getGender());
        user.setPhoto(registerRequest.getPhoto());
        user.setEnabled(true);
        user.setLocked(false);
    }

    /**
     * Request a password reset
     *
     * @param resetRequest the password reset request
     * @return the password reset response
     */
    @Transactional
    public PasswordResetResponse requestPasswordReset(PasswordResetRequest resetRequest) {
        log.debug("Password reset requested for email: {}", resetRequest.getEmail());

        // Find user by email
        Optional<User> userOptional = userRepository.findByEmail(resetRequest.getEmail());
        if (userOptional.isEmpty()) {
            // Don't reveal that the email doesn't exist
            return PasswordResetResponse.builder()
                    .success(true)
                    .message("If your email is registered, you will receive a password reset link")
                    .build();
        }

        User user = userOptional.get();

        // Generate reset token
        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDate.now().plusDays(1)); // Token valid for 24 hours
        userRepository.save(user);

        // Send password reset email
        sendPasswordResetEmail(user, resetToken);

        return PasswordResetResponse.builder()
                .success(true)
                .message("If your email is registered, you will receive a password reset link")
                .build();
    }

    /**
     * Confirm password reset with user provided password
     *
     * @param confirmRequest the password reset confirmation request
     * @return the password reset response
     */
    @Transactional
    public PasswordResetResponse confirmPasswordReset(PasswordResetConfirmRequest confirmRequest) {
        log.debug("Password reset confirmation requested for token: {}", confirmRequest.getToken());

        // Find user by reset token
        User user = userRepository.findByResetToken(confirmRequest.getToken())
                .orElseThrow(() -> new ServiceException.ValidationException("Invalid or expired reset token"));

        // Check if token is expired
        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDate.now())) {
            throw new ServiceException.ValidationException("Reset token has expired");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(confirmRequest.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        // Send password changed confirmation email
        sendPasswordChangedEmail(user);

        return PasswordResetResponse.builder()
                .success(true)
                .message("Your password has been successfully reset")
                .build();
    }

    /**
     * Change password for authenticated user
     *
     * @param changeRequest the change password request
     * @return the password reset response
     */
    @Transactional
    public PasswordResetResponse changePassword(ChangePasswordRequest changeRequest) {
        // Get the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("User", "username", username));

        // Verify current password
        if (!passwordEncoder.matches(changeRequest.getCurrentPassword(), user.getPassword())) {
            throw new ServiceException.ValidationException("Current password is incorrect");
        }

        // Verify that new password and confirm password match
        if (!changeRequest.getNewPassword().equals(changeRequest.getConfirmPassword())) {
            throw new ServiceException.ValidationException("New password and confirm password do not match");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(changeRequest.getNewPassword()));
        userRepository.save(user);

        // Send password changed confirmation email
        sendPasswordChangedEmail(user);

        return PasswordResetResponse.builder()
                .success(true)
                .message("Your password has been successfully changed")
                .build();
    }

    /**
     * Send welcome email with credentials
     *
     * @param user the user
     * @param plainPassword the plain password
     */
    private void sendWelcomeEmail(User user, String plainPassword) {
        String subject = "Welcome to School Management System";
        String content = String.format(
                "Dear %s %s,\n\n" +
                        "Welcome to our School Management System! Your account has been created.\n\n" +
                        "Here are your login credentials:\n" +
                        "Username: %s\n" +
                        "Password: %s\n\n" +
                        "Please change your password after your first login.\n\n" +
                        "Best regards,\n" +
                        "School Management Team",
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                plainPassword
        );

        emailService.sendEmail(user.getEmail(), subject, content);
    }

    /**
     * Send password reset email
     *
     * @param user the user
     * @param resetToken the reset token
     */
    private void sendPasswordResetEmail(User user, String resetToken) {
        String subject = "Password Reset Request";
        String resetLink = "http://yourdomain.com/reset-password?token=" + resetToken;
        String content = String.format(
                "Dear %s %s,\n\n" +
                        "You have requested to reset your password. Please click the link below to set a new password:\n\n" +
                        "%s\n\n" +
                        "This link will expire in 24 hours.\n\n" +
                        "If you did not request a password reset, please ignore this email.\n\n" +
                        "Best regards,\n" +
                        "School Management Team",
                user.getFirstName(),
                user.getLastName(),
                resetLink
        );

        emailService.sendEmail(user.getEmail(), subject, content);
    }

    /**
     * Send password changed confirmation email
     *
     * @param user the user
     */
    private void sendPasswordChangedEmail(User user) {
        String subject = "Password Changed Confirmation";
        String content = String.format(
                "Dear %s %s,\n\n" +
                        "Your password has been successfully changed.\n\n" +
                        "If you did not make this change, please contact the administrator immediately.\n\n" +
                        "Best regards,\n" +
                        "School Management Team",
                user.getFirstName(),
                user.getLastName()
        );

        emailService.sendEmail(user.getEmail(), subject, content);
    }

    private Role determineEffectiveRole(User user) {
        if (user instanceof Administrator) {
            return Role.ADMINISTRATOR;
        } else if (user instanceof Teacher) {
            return Role.TEACHER;
        } else if (user instanceof Student) {
            return Role.STUDENT;
        } else if (user instanceof Parent) {
            return Role.PARENT;
        }
        return user.getRole();
    }
}