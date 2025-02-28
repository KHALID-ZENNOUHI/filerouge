package org.dev.filerouge.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.dev.filerouge.domain.Class;

import java.util.UUID;

@Service
public interface IClassService {
    Class save(Class aClass);

    Class update(Class aClass);

    Class findById(UUID id);

    void delete(UUID id);

    Page<Class> findAll(int page, int size);
}
