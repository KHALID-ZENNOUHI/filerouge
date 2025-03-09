package org.dev.filerouge.repository;

import org.dev.filerouge.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for the User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find user by username
     *
     * @param username the username
     * @return the user
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     *
     * @param email the email
     * @return the user
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by reset token
     *
     * @param resetToken the reset token
     * @return the user
     */
    Optional<User> findByResetToken(String resetToken);


    /**
     * Check if a user with the given username exists
     *
     * @param username the username
     * @return true if a user with the username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Check if a user with the given email exists
     *
     * @param email the email
     * @return true if a user with the email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Check if a user with the given CIN exists
     *
     * @param cin the CIN
     * @return true if a user with the CIN exists, false otherwise
     */
    boolean existsByCin(String cin);

    /**
     * Find all users by role
     *
     * @param role the role
     * @param pageable the pagination information
     * @return a page of users
     */
    Page<User> findByRole(String role, Pageable pageable);

    /**
     * Search users by first name or last name containing the search term
     *
     * @param searchTerm the search term
     * @param pageable the pagination information
     * @return a page of users
     */
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<User> searchByName(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Count the number of users by role
     *
     * @param role the role
     * @return the number of users
     */
    long countByRole(String role);

    /**
     * Find inactive users (no login for a specified number of days)
     *
     * @param inactiveDate the date
     * @param pageable the pagination information
     * @return a page of users
     */
    Page<User> findByLastLoginBefore(LocalDate inactiveDate, Pageable pageable);

    /**
     * Find users with no login date (never logged in)
     *
     * @param pageable the pagination information
     * @return a page of users
     */
    Page<User> findByLastLoginIsNull(Pageable pageable);

    /**
     * Find users by enabled status
     *
     * @param enabled the enabled status
     * @param pageable the pagination information
     * @return a page of users
     */
    Page<User> findByEnabled(boolean enabled, Pageable pageable);

    /**
     * Find users by locked status
     *
     * @param locked the locked status
     * @param pageable the pagination information
     * @return a page of users
     */
    Page<User> findByLocked(boolean locked, Pageable pageable);



    /**
     * Count enabled and not locked users (active users)
     *
     * @return the number of active users
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.enabled = true AND u.locked = false")
    long countActiveUsers();

    /**
     * Count locked users
     *
     * @return the number of locked users
     */
    long countByLocked(boolean locked);

    /**
     * Find users that match multiple criteria
     *
     * @param role the role
     * @param enabled the enabled status
     * @param locked the locked status
     * @param pageable the pagination information
     * @return a page of users
     */
    Page<User> findByRoleAndEnabledAndLocked(String role, boolean enabled, boolean locked, Pageable pageable);


}