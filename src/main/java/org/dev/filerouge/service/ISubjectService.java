package org.dev.filerouge.service;

import org.dev.filerouge.domain.Subject;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ISubjectService {
    Subject save(Subject subject);

    Subject update(Subject subject);

    Subject findById(UUID id);

    void delete(UUID id);

    Page<Subject> findAll(int page, int size);
}
