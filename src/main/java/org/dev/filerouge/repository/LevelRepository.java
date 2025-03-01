package org.dev.filerouge.repository;

import org.dev.filerouge.domain.Department;
import org.dev.filerouge.domain.Level;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LevelRepository extends JpaRepository<Level, UUID> {

    /**
     * Finds a level by its name
     *
     * @param name the name to search for
     * @return an optional containing the found level, or empty if not found
     */
    Optional<Level> findByName(String name);

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
     * @param department the department
     * @return the list of levels
     */
    List<Level> findByDepartment(Department department);

    /**
     * Finds all levels for a specific department with pagination
     *
     * @param department the department
     * @param pageable the pagination information
     * @return a page of levels
     */
    Page<Level> findByDepartment(Department department, Pageable pageable);

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
     * @param pageable the pagination information
     * @return a page of levels
     */
    Page<Level> findByDepartmentId(UUID departmentId, Pageable pageable);

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
     * Finds levels by name containing the search term (case insensitive)
     *
     * @param name the search term
     * @return the list of matching levels
     */
    List<Level> findByNameContainingIgnoreCase(String name);

    /**
     * Counts the number of classes in a specific level through the class relationship
     *
     * @param levelId the level ID
     * @return the number of classes
     */
    @Query("SELECT COUNT(c) FROM Class c WHERE c.level.id = :levelId")
    long countClassesByLevelId(UUID levelId);
}