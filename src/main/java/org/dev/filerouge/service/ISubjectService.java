package org.dev.filerouge.service;

import org.dev.filerouge.domain.Subject;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing {@link Subject} entities.
 */
public interface ISubjectService extends BaseService<Subject> {

    /**
     * Finds a subject by its name
     *
     * @param name the name to search for
     * @return the found subject
     */
    Subject findByName(String name);

    /**
     * Checks if a subject with the given name exists
     *
     * @param name the name to check
     * @return true if a subject with the name exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Finds all subjects associated with a specific class through program relationship
     *
     * @param classId the class ID
     * @return the list of subjects
     */
    List<Subject> findByClassId(UUID classId);

    /**
     * Finds all subjects for a specific program
     *
     * @param programId the program ID
     * @return the list of subjects
     */
    List<Subject> findByProgramId(UUID programId);

    /**
     * Searches for subjects by name (partial match, case insensitive)
     *
     * @param searchTerm the search term
     * @return the list of matching subjects
     */
    List<Subject> searchByName(String searchTerm);

    /**
     * Assigns a subject to a program
     *
     * @param subjectId the subject ID
     * @param programId the program ID
     * @return the updated subject
     */
    Subject assignToProgram(UUID subjectId, UUID programId);

    /**
     * Removes a subject from its program
     *
     * @param subjectId the subject ID
     * @return the updated subject
     */
    Subject removeFromProgram(UUID subjectId);
}