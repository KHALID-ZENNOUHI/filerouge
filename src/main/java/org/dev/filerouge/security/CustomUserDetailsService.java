package org.dev.filerouge.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dev.filerouge.domain.*;
import org.dev.filerouge.domain.Enum.Role;
import org.dev.filerouge.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Custom UserDetailsService implementation that loads user details from the database
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            log.debug("Loading user by username: {}", username);
            return userRepository.findByUsername(username)
                    .map(this::createSpringSecurityUser)
                    .orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " not found"));
        } catch (Exception e) {
            log.error("Error loading user by username: {}", username, e);
            throw e;
        }
    }

    private org.springframework.security.core.userdetails.User createSpringSecurityUser(User user) {
        try {
            // Determine role based on instance type
            Role effectiveRole = determineEffectiveRole(user);
            log.debug("Determined effective role for user {}: {}", user.getUsername(), effectiveRole);

            // Create authorities from role and permissions
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();

            if (effectiveRole != null) {
                // Add permissions as authorities
                authorities.addAll(effectiveRole.getPermissions().stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList()));

                // Add the role itself as an authority (without adding another ROLE_ prefix)
                authorities.add(new SimpleGrantedAuthority("ROLE_" + effectiveRole.name()));
            }

            log.debug("Created authorities for user {}: {}", user.getUsername(), authorities);

            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    user.isEnabled(),
                    true, // accountNonExpired
                    true, // credentialsNonExpired
                    !user.isLocked(), // accountNonLocked
                    authorities
            );
        } catch (Exception e) {
            log.error("Error creating Spring Security user for {}: {}", user.getUsername(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Determine the effective role based on the user's instance type
     */
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
        // Fallback - try to use the role field directly if instance type doesn't match
        return user.getRole();
    }
}