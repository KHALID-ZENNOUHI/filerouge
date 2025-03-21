package org.dev.filerouge.repository;

import org.dev.filerouge.domain.Program;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProgramRepository extends JpaRepository<Program, UUID> {
    // Find programs by their ID
    Optional<Program> findById(UUID id);

    // Count programs with classes or subjects related to specific entities
    long countByClassesId(UUID classId);
    long countBySubjectsId(UUID subjectId);

    // Check if a program has specific class and subject
    boolean existsByClassesIdAndSubjectsId(UUID classId, UUID subjectId);

    // Get paginated results
    Page<Program> findAll(Pageable pageable);
}