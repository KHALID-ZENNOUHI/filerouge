package org.dev.filerouge.repository;

import org.dev.filerouge.domain.Class;
import org.dev.filerouge.domain.Level;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClassRepository extends JpaRepository<Class, UUID> {

    /**
     * Finds a class by its name
     *
     * @param name the name to search for
     * @return an optional containing the found class, or empty if not found
     */
    Optional<Class> findByName(String name);

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
     * @param level the level
     * @return the list of classes
     */
    List<Class> findByLevel(Level level);

    /**
     * Finds all classes for a specific level with pagination
     *
     * @param level the level
     * @param pageable the pagination information
     * @return a page of classes
     */
    Page<Class> findByLevel(Level level, Pageable pageable);

    /**
     * Finds all classes for a specific department through level relationship
     *
     * @param departmentId the department ID
     * @return the list of classes
     */
    List<Class> findByLevelDepartmentId(UUID departmentId);

    /**
     * Counts the number of classes for a specific level
     *
     * @param levelId the level ID
     * @return the number of classes
     */
    long countByLevelId(UUID levelId);
}