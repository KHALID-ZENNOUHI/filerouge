package org.dev.filerouge.service;

import org.dev.filerouge.domain.Program;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IProgramService extends BaseService<Program> {
    /**
     * Find all programs with pagination
     *
     * @param page the page number
     * @param size the page size
     * @return paginated list of programs
     */
    Page<Program> findAll(int page, int size);

    /**
     * Find programs by class ID
     *
     * @param classId the class ID
     * @return list of programs containing the class
     */
    List<Program> findByClassId(UUID classId);

    /**
     * Find programs by class ID with pagination
     *
     * @param classId the class ID
     * @param page the page number
     * @param size the page size
     * @return paginated list of programs containing the class
     */
    Page<Program> findByClassId(UUID classId, int page, int size);

    /**
     * Find programs by subject ID
     *
     * @param subjectId the subject ID
     * @return list of programs containing the subject
     */
    List<Program> findBySubjectId(UUID subjectId);

    /**
     * Find programs by subject ID with pagination
     *
     * @param subjectId the subject ID
     * @param page the page number
     * @param size the page size
     * @return paginated list of programs containing the subject
     */
    Page<Program> findBySubjectId(UUID subjectId, int page, int size);

    /**
     * Find a program by class ID and subject ID
     *
     * @param classId the class ID
     * @param subjectId the subject ID
     * @return the program containing both class and subject
     */
    Program findByClassAndSubject(UUID classId, UUID subjectId);

    /**
     * Check if a program exists with the given class and subject
     *
     * @param classId the class ID
     * @param subjectId the subject ID
     * @return true if a program exists with the class and subject
     */
    boolean existsByClassAndSubject(UUID classId, UUID subjectId);

    /**
     * Get statistics about programs associated with a class
     *
     * @param classId the class ID
     * @return map of statistics
     */
    Map<String, Object> getClassProgramStatistics(UUID classId);

    /**
     * Get statistics about programs associated with a subject
     *
     * @param subjectId the subject ID
     * @return map of statistics
     */
    Map<String, Object> getSubjectProgramStatistics(UUID subjectId);

    /**
     * Count programs by class ID
     *
     * @param classId the class ID
     * @return the count of programs
     */
    long countByClassId(UUID classId);

    /**
     * Count programs by subject ID
     *
     * @param subjectId the subject ID
     * @return the count of programs
     */
    long countBySubjectId(UUID subjectId);

    /**
     * Delete all programs for a class
     *
     * @param classId the class ID
     */
    void deleteByClassId(UUID classId);

    /**
     * Delete all programs for a subject
     *
     * @param subjectId the subject ID
     */
    void deleteBySubjectId(UUID subjectId);

    /**
     * Assign a subject to a class in a program
     *
     * @param classId the class ID
     * @param subjectId the subject ID
     * @param description the program description
     * @return the program with the class and subject
     */
    Program assignSubjectToClass(UUID classId, UUID subjectId, String description);

    /**
     * Remove a subject from a class
     *
     * @param classId the class ID
     * @param subjectId the subject ID
     */
    void removeSubjectFromClass(UUID classId, UUID subjectId);

    /**
     * Check if a program exists by ID
     *
     * @param id the program ID
     * @return true if the program exists
     */
    boolean existsById(UUID id);
}