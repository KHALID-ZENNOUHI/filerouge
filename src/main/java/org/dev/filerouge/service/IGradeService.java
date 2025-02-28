package org.dev.filerouge.service;

import org.dev.filerouge.domain.Grade;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface IGradeService {
    Grade save(Grade grade);

    Grade update(Grade grade);

    Grade findById(UUID id);

    void delete(UUID id);

    Page<Grade> findAll(int page, int size);
}
