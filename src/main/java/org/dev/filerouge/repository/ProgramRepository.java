package org.dev.filerouge.repository;

import org.dev.filerouge.domain.Class;
import org.dev.filerouge.domain.Program;
import org.dev.filerouge.domain.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProgramRepository extends JpaRepository<Program, UUID> {

    /**
     * Finds all programs for a specific class
     *
     * @param classes the class
     * @return the list of programs
     */
    List<Program> findByClasses(Class classes);

    /**
     * Finds all programs for a specific class with pagination
     *
     * @param classes the class
     * @param pageable the pagination information
     * @return a page of programs
     */
    Page<Program> findByClasses(Class classes, Pageable pageable);

    /**
     * Finds all programs for a specific class ID
     *
     * @param classId the class ID
     * @return the list of programs
     */
    List<Program> findByClassesId(UUID classId);

    /**
     * Finds all programs for a specific subject
     *
     * @param subject the subject
     * @return the list of programs
     */
    List<Program> findBySubject(Subject subject);

    /**
     * Finds all programs for a specific subject with pagination
     *
     * @param subject the subject
     * @param pageable the pagination information
     * @return a page of programs
     */
    Page<Program> findBySubject(Subject subject, Pageable pageable);

    /**
     * Finds all programs for a specific subject ID
     *
     * @param subjectId the subject ID
     * @return the list of programs
     */
    List<Program> findBySubjectId(UUID subjectId);

    /**
     * Finds a program by class and subject
     *
     * @param classId the class ID
     * @param subjectId the subject ID
     * @return an optional containing the found program, or empty if not found
     */
    Optional<Program> findByClassesIdAndSubjectId(UUID classId, UUID subjectId);

    /**
     * Checks if a program exists for a specific class and subject
     *
     * @param classId the class ID
     * @param subjectId the subject ID
     * @return true if a program exists for the class and subject, false otherwise
     */
    boolean existsByClassesIdAndSubjectId(UUID classId, UUID subjectId);

    /**
     * Counts the number of programs for a specific class
     *
     * @param classId the class ID
     * @return the number of programs
     */
    long countByClassesId(UUID classId);

    /**
     * Counts the number of programs for a specific subject
     *
     * @param subjectId the subject ID
     * @return the number of programs
     */
    long countBySubjectId(UUID subjectId);

    /**
     * Deletes all programs for a specific class
     *
     * @param classId the class ID
     */
    void deleteByClassesId(UUID classId);

    /**
     * Deletes all programs for a specific subject
     *
     * @param subjectId the subject ID
     */
    void deleteBySubjectId(UUID subjectId);
}