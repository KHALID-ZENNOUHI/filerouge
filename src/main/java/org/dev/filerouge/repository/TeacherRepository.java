package org.dev.filerouge.repository;

import org.dev.filerouge.domain.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, UUID> {

    /**
     * Finds a teacher by username
     *
     * @param username the username to search for
     * @return an optional containing the found teacher, or empty if not found
     */
    Optional<Teacher> findByUsername(String username);

    /**
     * Finds all teachers who teach a specific subject
     *
     * @param subjectId the subject ID
     * @return the list of teachers
     */
    List<Teacher> findBySessionsSubjectId(UUID subjectId);

    /**
     * Finds teachers by last name (partial match, case insensitive)
     *
     * @param lastName the last name to search for
     * @return the list of matching teachers
     */
    List<Teacher> findByLastNameContainingIgnoreCase(String lastName);

    /**
     * Finds teachers by first name (partial match, case insensitive)
     *
     * @param firstName the first name to search for
     * @return the list of matching teachers
     */
    List<Teacher> findByFirstNameContainingIgnoreCase(String firstName);

    /**
     * Searches for teachers by first name or last name (partial match, case insensitive)
     *
     * @param searchTerm the search term
     * @param pageable the pagination information
     * @return a page of matching teachers
     */
    @Query("SELECT t FROM Teacher t WHERE LOWER(t.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(t.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Teacher> searchByName(String searchTerm, Pageable pageable);
}