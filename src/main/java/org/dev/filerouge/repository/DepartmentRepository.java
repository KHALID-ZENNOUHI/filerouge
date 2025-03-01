package org.dev.filerouge.repository;

import org.dev.filerouge.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {

    /**
     * Finds a department by its name
     *
     * @param name the name to search for
     * @return an optional containing the found department, or empty if not found
     */
    Optional<Department> findByName(String name);

    /**
     * Checks if a department with the given name exists
     *
     * @param name the name to check
     * @return true if a department with the name exists, false otherwise
     */
    boolean existsByName(String name);
}