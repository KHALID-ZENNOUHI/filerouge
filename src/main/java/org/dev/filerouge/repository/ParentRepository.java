package org.dev.filerouge.repository;

import org.dev.filerouge.domain.Parent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for the Parent entity.
 */
@Repository
public interface ParentRepository extends JpaRepository<Parent, UUID> {

    /**
     * Find parent by username
     *
     * @param username the username
     * @return the parent
     */
    Optional<Parent> findByUsername(String username);

    /**
     * Find parent by email
     *
     * @param email the email
     * @return the parent
     */
    Optional<Parent> findByEmail(String email);

    /**
     * Find parent by CIN
     *
     * @param cin the CIN
     * @return the parent
     */
    Optional<Parent> findByCin(String cin);

    /**
     * Find parents by first name containing the search term (case insensitive)
     *
     * @param firstName the search term
     * @return the list of matching parents
     */
    List<Parent> findByFirstNameContainingIgnoreCase(String firstName);

    /**
     * Find parents by last name containing the search term (case insensitive)
     *
     * @param lastName the search term
     * @return the list of matching parents
     */
    List<Parent> findByLastNameContainingIgnoreCase(String lastName);

    /**
     * Find parents with students in a specific class
     *
     * @param classId the class ID
     * @return the list of parents
     */
    @Query("SELECT DISTINCT p FROM Parent p JOIN Student s ON s.parent = p WHERE s.studentClass.id = :classId")
    List<Parent> findByStudentsClassId(UUID classId);

    /**
     * Find parents with students in a specific class with pagination
     *
     * @param classId the class ID
     * @param pageable the pagination information
     * @return a page of parents
     */
    @Query("SELECT DISTINCT p FROM Parent p JOIN Student s ON s.parent = p WHERE s.studentClass.id = :classId")
    Page<Parent> findByStudentsClassId(UUID classId, Pageable pageable);

    /**
     * Find parents by enabled status
     *
     * @param enabled the enabled status
     * @param pageable the pagination information
     * @return a page of parents
     */
    Page<Parent> findByEnabled(boolean enabled, Pageable pageable);

    /**
     * Find parents by locked status
     *
     * @param locked the locked status
     * @param pageable the pagination information
     * @return a page of parents
     */
    Page<Parent> findByLocked(boolean locked, Pageable pageable);

    /**
     * Search parents by first name or last name containing the search term
     *
     * @param searchTerm the search term
     * @param pageable the pagination information
     * @return a page of parents
     */
    @Query("SELECT p FROM Parent p WHERE " +
            "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Parent> searchByName(String searchTerm, Pageable pageable);

    /**
     * Find parents with no students
     *
     * @param pageable the pagination information
     * @return a page of parents
     */
    @Query("SELECT p FROM Parent p WHERE NOT EXISTS (SELECT s FROM Student s WHERE s.parent = p)")
    Page<Parent> findParentsWithNoStudents(Pageable pageable);

    /**
     * Count parents with at least one student
     *
     * @return the number of parents with students
     */
    @Query("SELECT COUNT(DISTINCT p) FROM Parent p JOIN Student s ON s.parent = p")
    long countParentsWithStudents();

    /**
     * Count parents with no students
     *
     * @return the number of parents with no students
     */
    @Query("SELECT COUNT(p) FROM Parent p WHERE NOT EXISTS (SELECT s FROM Student s WHERE s.parent = p)")
    long countParentsWithNoStudents();

    /**
     * Count active parents (enabled and not locked)
     *
     * @return the number of active parents
     */
    @Query("SELECT COUNT(p) FROM Parent p WHERE p.enabled = true AND p.locked = false")
    long countActiveParents();
}