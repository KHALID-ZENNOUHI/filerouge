package org.dev.filerouge.service;

import org.dev.filerouge.domain.Department;

/**
 * Service interface for managing {@link Department} entities.
 */
public interface IDepartmentService extends BaseService<Department> {

    /**
     * Finds a department by its name
     *
     * @param name the name to search for
     * @return the found department, or null if not found
     */
    Department findByName(String name);

    /**
     * Checks if a department with the given name exists
     *
     * @param name the name to check
     * @return true if a department with the name exists, false otherwise
     */
    boolean existsByName(String name);
}