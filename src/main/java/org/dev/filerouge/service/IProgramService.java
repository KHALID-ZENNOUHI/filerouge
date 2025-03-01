package org.dev.filerouge.service;

import org.dev.filerouge.domain.Program;
import org.dev.filerouge.service.BaseService;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service interface for managing {@link Program} entities.
 */
public interface IProgramService extends BaseService<Program> {

    /**
     * Finds all programs for a specific class
     *
     * @param classId the class ID
     * @return the list of programs
     */
    List<Program> findByClassId(UUID classId);

    /**
     * Finds all programs for a specific class with pagination
     *
     * @param classId the class ID
     * @param page the page number (0-based)
     * @param size the page size
     * @return a page of programs
     */
    Page<Program> findByClassId(UUID classId, int page, int size);

    /**
     * Finds all programs for a specific subject
     *
     * @param subjectId the subject ID
     * @return the list of programs
     */
    List<Program> findBySubjectId(UUID subjectId);

    /**
     * Finds all programs for a specific subject with pagination
     *
     * @param subjectId the subject ID
     * @param page the page number (0-based)
     * @param size the page size
     * @return a page of programs
     */
    Page<Program> findBySubjectId(UUID subjectId, int page, int size);

    /**
     * Finds a program by class and subject
     *
     * @param classId the class ID
     * @param subjectId the subject ID
     * @return the found program
     */
    Program findByClassAndSubject(UUID classId, UUID subjectId);

    /**
     * Checks if a program exists for a specific class and subject
     *
     * @param classId the class ID
     * @param subjectId the subject ID
     * @return true if a program exists for the class and subject, false otherwise
     */
    boolean existsByClassAndSubject(UUID classId, UUID subjectId);

    /**
     * Gets program statistics for a class
     *
     * @param classId the class ID
     * @return a map of statistics
     */
    Map<String, Object> getClassProgramStatistics(UUID classId);

    /**
     * Gets program statistics for a subject
     *
     * @param subjectId the subject ID
     * @return a map of statistics
     */
    Map<String, Object> getSubjectProgramStatistics(UUID subjectId);

    /**
     * Counts the number of programs for a specific class
     *
     * @param classId the class ID
     * @return the number of programs
     */
    long countByClassId(UUID classId);

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
    void deleteByClassId(UUID classId);

    /**
     * Deletes all programs for a specific subject
     *
     * @param subjectId the subject ID
     */
    void deleteBySubjectId(UUID subjectId);

    /**
     * Assigns a subject to a class
     *
     * @param classId the class ID
     * @param subjectId the subject ID
     * @param description the program description
     * @return the created program
     */
    Program assignSubjectToClass(UUID classId, UUID subjectId, String description);

    /**
     * Removes a subject from a class
     *
     * @param classId the class ID
     * @param subjectId the subject ID
     */
    void removeSubjectFromClass(UUID classId, UUID subjectId);
}