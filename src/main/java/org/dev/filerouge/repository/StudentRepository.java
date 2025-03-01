package org.dev.filerouge.repository;

import org.dev.filerouge.domain.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {

    /**
     * Finds a student by username
     *
     * @param username the username to search for
     * @return an optional containing the found student, or empty if not found
     */
    Optional<Student> findByUsername(String username);

    /**
     * Finds all students in a specific class
     *
     * @param classId the class ID
     * @return the list of students
     */
    List<Student> findByStudentClassId(UUID classId);

    /**
     * Finds all students in a specific class with pagination
     *
     * @param classId the class ID
     * @param pageable the pagination information
     * @return a page of students
     */
    Page<Student> findByStudentClassId(UUID classId, Pageable pageable);

    /**
     * Finds all students with a specific parent
     *
     * @param parentId the parent ID
     * @return the list of students
     */
    List<Student> findByParentId(UUID parentId);

    /**
     * Finds students by last name (partial match, case insensitive)
     *
     * @param lastName the last name to search for
     * @return the list of matching students
     */
    List<Student> findByLastNameContainingIgnoreCase(String lastName);

    /**
     * Searches for students by first name or last name (partial match, case insensitive)
     *
     * @param searchTerm the search term
     * @param pageable the pagination information
     * @return a page of matching students
     */
    @Query("SELECT s FROM Student s WHERE LOWER(s.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Student> searchByName(String searchTerm, Pageable pageable);

    /**
     * Counts the number of students in a specific class
     *
     * @param classId the class ID
     * @return the number of students
     */
    long countByStudentClassId(UUID classId);
}