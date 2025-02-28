package org.dev.filerouge.service.implementation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.dev.filerouge.config.JwtUtil;
import org.dev.filerouge.domain.Administrator;
import org.dev.filerouge.domain.Enum.Role;
import org.dev.filerouge.domain.Student;
import org.dev.filerouge.domain.Teacher;
import org.dev.filerouge.domain.User;
import org.dev.filerouge.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MyUserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    @PersistenceContext
    private EntityManager entityManager;

    public String login(User user) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                user.getUsername(), user.getPassword()));

        UserDetails userDetails = userService.loadUserByUsername(user.getUsername());
        return jwtUtil.generateToken(userDetails);
    }

    @Transactional
    public void register(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        User userToSave;
        switch (user.getRole()) {
            case ADMINISTRATOR -> userToSave = new Administrator();
            case STUDENT -> userToSave = new Student();
            case TEACHER -> userToSave = new Teacher();
            default -> throw new IllegalArgumentException("Role not found: " + user.getRole());
        }


        // Map common fields
        userToSave.setUsername(user.getUsername());
        userToSave.setFirstName(user.getFirstName());
        userToSave.setLastName(user.getLastName());
        userToSave.setEmail(user.getEmail());
        userToSave.setPassword(passwordEncoder.encode(user.getPassword()));
        userToSave.setCin(user.getCin());
        userToSave.setPhone(user.getPhone());
        userToSave.setBirthDate(user.getBirthDate());
        userToSave.setBirthPlace(user.getBirthPlace());
        userToSave.setAddress(user.getAddress());
        userToSave.setGender(user.getGender());
        userToSave.setPhoto(user.getPhoto());

        entityManager.persist(userToSave);
    }

    // Get User By ID
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + id));
    }

    // Get All Users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Update User Information
    public User updateUser(UUID id, User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setUsername(updatedUser.getUsername());
                    user.setPassword(updatedUser.getPassword());
                    user.setEmail(updatedUser.getEmail());
                    user.setRole(updatedUser.getRole());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + id));
    }

    // Delete User By ID
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UsernameNotFoundException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }
    
    public User mapUserToRoleAndEncodePassword(User user, User userWithRole){
        userWithRole.setId(user.getId());
        userWithRole.setUsername(user.getUsername());
        userWithRole.setFirstName(user.getFirstName());
        userWithRole.setLastName(user.getLastName());
        userWithRole.setEmail(user.getEmail());
        userWithRole.setPassword(user.getPassword());
        userWithRole.setRole(user.getRole());
        userWithRole.setBirthDate(user.getBirthDate());
        userWithRole.setBirthPlace(user.getBirthPlace());
        userWithRole.setGender(user.getGender());
        userWithRole.setAddress(user.getAddress());
        userWithRole.setCin(user.getCin());
        userWithRole.setPhone(user.getPhone());
        userWithRole.setPhoto(user.getPhoto());
        userWithRole.setPassword(passwordEncoder.encode(userWithRole.getPassword()));
        return userWithRole;
    }
}