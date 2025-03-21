package org.dev.filerouge.repository;

import org.dev.filerouge.domain.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, UUID> {

    /**
     * Finds a subject by its name
     *
     * @param name the name to search for
     * @return an optional containing the found subject, or empty if not found
     */
    Optional<Subject> findByName(String name);

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
    @Query("SELECT s FROM Subject s WHERE s.program.id IN " +
            "(SELECT c.program.id FROM Class c WHERE c.id = :classId)")
    List<Subject> findByProgramClassId(@Param("classId") UUID classId);

    /**
     * Finds all subjects by name containing the search term (case insensitive)
     *
     * @param name the search term
     * @return the list of matching subjects
     */
    List<Subject> findByNameContainingIgnoreCase(String name);

    /**
     * Finds all subjects for a specific program
     *
     * @param programId the program ID
     * @return the list of subjects
     */
    List<Subject> findByProgramId(UUID programId);
}