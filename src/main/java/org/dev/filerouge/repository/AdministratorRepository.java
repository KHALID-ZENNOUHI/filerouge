package org.dev.filerouge.repository;

import org.dev.filerouge.domain.Administrator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for the Administrator entity.
 */
@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, UUID> {

    /**
     * Find administrators by first name containing the search term (case insensitive)
     *
     * @param firstName the search term
     * @return the list of matching administrators
     */
    List<Administrator> findByFirstNameContainingIgnoreCase(String firstName);

    /**
     * Find administrators by last name containing the search term (case insensitive)
     *
     * @param lastName the search term
     * @return the list of matching administrators
     */
    List<Administrator> findByLastNameContainingIgnoreCase(String lastName);

    /**
     * Find administrators by email containing the search term (case insensitive)
     *
     * @param email the search term
     * @return the list of matching administrators
     */
    List<Administrator> findByEmailContainingIgnoreCase(String email);

    /**
     * Find administrators by enabled status
     *
     * @param enabled the enabled status
     * @param pageable the pagination information
     * @return a page of administrators
     */
    Page<Administrator> findByEnabled(boolean enabled, Pageable pageable);

    /**
     * Find administrators by locked status
     *
     * @param locked the locked status
     * @param pageable the pagination information
     * @return a page of administrators
     */
    Page<Administrator> findByLocked(boolean locked, Pageable pageable);

    /**
     * Find administrators by last login date before the given date
     *
     * @param date the date
     * @param pageable the pagination information
     * @return a page of administrators
     */
    Page<Administrator> findByLastLoginBefore(LocalDate date, Pageable pageable);

    /**
     * Find administrators who have never logged in
     *
     * @param pageable the pagination information
     * @return a page of administrators
     */
    Page<Administrator> findByLastLoginIsNull(Pageable pageable);

    /**
     * Search administrators by first name or last name containing the search term
     *
     * @param searchTerm the search term
     * @param pageable the pagination information
     * @return a page of administrators
     */
    @Query("SELECT a FROM Administrator a WHERE " +
            "LOWER(a.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Administrator> searchByName(String searchTerm, Pageable pageable);

    /**
     * Count active administrators (enabled and not locked)
     *
     * @return the number of active administrators
     */
    @Query("SELECT COUNT(a) FROM Administrator a WHERE a.enabled = true AND a.locked = false")
    long countActiveAdministrators();
}