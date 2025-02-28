package org.dev.filerouge.service;

import org.dev.filerouge.domain.Level;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ILevelService {
    Level save(Level level);

    Level update(Level level);

    Level findById(UUID id);

    void delete(UUID id);

    Page<Level> findAll(int page, int size);
}
