package org.dev.filerouge.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Component responsible for generating and validating JWT tokens.
 */
@Component
@Slf4j
public class JwtTokenProvider {

    private static final String AUTHORITIES_KEY = "auth";

    private final Key key;
    private final JwtParser jwtParser;
    private final long tokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.token-validity-in-seconds:86400}") long tokenValidityInSeconds,
            @Value("${jwt.refresh-token-validity-in-seconds:2592000}") long refreshTokenValidityInSeconds) {

        // Set defaults if properties are not provided
        this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInSeconds * 1000;

        // Initialize the key properly to avoid "variable might already have been assigned" error
        this.key = initializeSigningKey(secret);
        this.jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
    }

    /**
     * Initialize the signing key, either from the provided secret or by generating a new one
     */
    private Key initializeSigningKey(String secret) {
        try {
            // Try to use the provided secret
            byte[] keyBytes = Decoders.BASE64.decode(secret);
            if (keyBytes.length >= 32) { // 256 bits minimum for HS256
                log.debug("Using provided secret key");
                return Keys.hmacShaKeyFor(keyBytes);
            } else {
                log.warn("Provided secret key is too short, using a generated key instead");
                return Keys.secretKeyFor(SignatureAlgorithm.HS256);
            }
        } catch (Exception e) {
            log.warn("Error processing JWT secret, using generated key: {}", e.getMessage());
            return Keys.secretKeyFor(SignatureAlgorithm.HS256);
        }
    }

    /**
     * Create a JWT token for the specified authentication
     *
     * @param authentication the authentication object
     * @return the JWT token
     */
    public String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(validity)
                .compact();
    }

    /**
     * Create a refresh token for the specified authentication
     *
     * @param authentication the authentication object
     * @return the refresh token
     */
    public String createRefreshToken(Authentication authentication) {
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.refreshTokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(validity)
                .compact();
    }

    /**
     * Get authentication from a JWT token
     *
     * @param token the JWT token
     * @return the authentication object
     */
    public Authentication getAuthentication(String token) {
        Claims claims = jwtParser.parseClaimsJws(token).getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .filter(auth -> !auth.trim().isEmpty())
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    /**
     * Validate a JWT token
     *
     * @param authToken the JWT token
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String authToken) {
        try {
            jwtParser.parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.info("Invalid JWT token: {}", e.getMessage());
            log.trace("Invalid JWT token trace.", e);
        }
        return false;
    }

    /**
     * Get username from token
     *
     * @param token the JWT token
     * @return the username
     */
    public String getUsernameFromToken(String token) {
        return jwtParser.parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Get expiration date from token
     *
     * @param token the JWT token
     * @return the expiration date
     */
    public Date getExpirationDateFromToken(String token) {
        return jwtParser.parseClaimsJws(token).getBody().getExpiration();
    }
}