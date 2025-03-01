package org.dev.filerouge.service;

import org.dev.filerouge.domain.Level;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing {@link Level} entities.
 */
public interface ILevelService extends BaseService<Level> {

    /**
     * Finds a level by its name
     *
     * @param name the name to search for
     * @return the found level
     */
    Level findByName(String name);

    /**
     * Checks if a level with the given name exists
     *
     * @param name the name to check
     * @return true if a level with the name exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Finds all levels for a specific department
     *
     * @param departmentId the department ID
     * @return the list of levels
     */
    List<Level> findByDepartmentId(UUID departmentId);

    /**
     * Finds all levels for a specific department with pagination
     *
     * @param departmentId the department ID
     * @param page the page number (0-based)
     * @param size the page size
     * @return a page of levels
     */
    Page<Level> findByDepartmentId(UUID departmentId, int page, int size);

    /**
     * Checks if a level with the given name exists in a specific department
     *
     * @param name the name to check
     * @param departmentId the department ID
     * @return true if a level with the name exists in the department, false otherwise
     */
    boolean existsByNameAndDepartmentId(String name, UUID departmentId);

    /**
     * Counts the number of levels in a specific department
     *
     * @param departmentId the department ID
     * @return the number of levels
     */
    long countByDepartmentId(UUID departmentId);

    /**
     * Searches for levels by name (partial match, case insensitive)
     *
     * @param searchTerm the search term
     * @return the list of matching levels
     */
    List<Level> searchByName(String searchTerm);

    /**
     * Counts the number of classes in a specific level
     *
     * @param levelId the level ID
     * @return the number of classes
     */
    long countClassesByLevelId(UUID levelId);

    /**
     * Gets the hierarchy path of a level (department > level)
     *
     * @param levelId the level ID
     * @return the hierarchy path as a string
     */
    String getLevelHierarchyPath(UUID levelId);
}