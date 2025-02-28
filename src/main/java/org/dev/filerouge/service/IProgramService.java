package org.dev.filerouge.service;

import org.dev.filerouge.domain.Program;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface IProgramService {
    Program save(Program program);

    Program update(Program program);

    Program findById(UUID id);

    void delete(UUID id);

    Page<Program> findAll(int page, int size);
}
