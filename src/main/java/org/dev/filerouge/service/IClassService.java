package org.dev.filerouge.service;

import org.dev.filerouge.domain.Class;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing {@link Class} entities.
 */
public interface IClassService extends BaseService<Class> {

    /**
     * Finds a class by its name
     *
     * @param name the name to search for
     * @return the found class
     */
    Class findByName(String name);

    /**
     * Checks if a class with the given name exists
     *
     * @param name the name to check
     * @return true if a class with the name exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Finds all classes for a specific level
     *
     * @param levelId the level ID
     * @return the list of classes
     */
    List<Class> findByLevelId(UUID levelId);

    /**
     * Finds all classes for a specific level with pagination
     *
     * @param levelId the level ID
     * @param page the page number (0-based)
     * @param size the page size
     * @return a page of classes
     */
    Page<Class> findByLevelId(UUID levelId, int page, int size);

    /**
     * Finds all classes for a specific department
     *
     * @param departmentId the department ID
     * @return the list of classes
     */
    List<Class> findByDepartmentId(UUID departmentId);

    /**
     * Counts the number of classes for a specific level
     *
     * @param levelId the level ID
     * @return the number of classes
     */
    long countByLevelId(UUID levelId);
}