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
     * @param clazz the class
     * @return the list of programs
     */
    List<Program> findByClazz(Class clazz);

    /**
     * Finds all programs for a specific class with pagination
     *
     * @param clazz the class
     * @param pageable the pagination information
     * @return a page of programs
     */
    Page<Program> findByClazz(Class clazz, Pageable pageable);

    /**
     * Finds all programs for a specific class ID
     *
     * @param clazzId the class ID
     * @return the list of programs
     */
    List<Program> findByClazzId(UUID clazzId);

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
     * @param clazzId the class ID
     * @param subjectId the subject ID
     * @return an optional containing the found program, or empty if not found
     */
    Optional<Program> findByClazzIdAndSubjectId(UUID clazzId, UUID subjectId);

    /**
     * Checks if a program exists for a specific class and subject
     *
     * @param clazzId the class ID
     * @param subjectId the subject ID
     * @return true if a program exists for the class and subject, false otherwise
     */
    boolean existsByClazzIdAndSubjectId(UUID clazzId, UUID subjectId);

    /**
     * Counts the number of programs for a specific class
     *
     * @param clazzId the class ID
     * @return the number of programs
     */
    long countByClazzId(UUID clazzId);

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
     * @param clazzId the class ID
     */
    void deleteByClazzId(UUID clazzId);

    /**
     * Deletes all programs for a specific subject
     *
     * @param subjectId the subject ID
     */
    void deleteBySubjectId(UUID subjectId);
}