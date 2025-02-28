package org.dev.filerouge.repository;

import org.dev.filerouge.domain.Program;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProgramRepository extends JpaRepository<Program, UUID> {
}
