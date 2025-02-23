package org.dev.filerouge.repository;

import org.dev.filerouge.domain.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface LevelRepository extends JpaRepository<Level, UUID> {
}
