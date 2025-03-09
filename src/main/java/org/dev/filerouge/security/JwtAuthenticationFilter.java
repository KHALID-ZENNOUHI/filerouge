package org.dev.filerouge.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dev.filerouge.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter for checking JWT tokens in the Authorization header and authenticating users.
 * Simplified version without token blacklist.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // Skip authentication for public endpoints
            if (request.getRequestURI().startsWith("/api/auth/") ||
                    request.getRequestURI().startsWith("/api/public/")) {
                log.debug("Skipping authentication for path: {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            String jwt = resolveToken(request);
            log.debug("Resolved JWT token: {}", jwt != null ? "present" : "absent");

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromToken(jwt);
                log.debug("Valid token for user: {}", username);

                userRepository.findByUsername(username).ifPresent(user -> {
                    if (user.isEnabled() && !user.isLocked()) {
                        Authentication authentication = tokenProvider.getAuthentication(jwt);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.debug("Set Authentication to security context for '{}'", authentication.getName());
                    } else {
                        log.warn("User account is disabled or locked: {}", username);
                    }
                });
            } else {
                log.debug("No valid JWT token found in request to {}", request.getRequestURI());
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}