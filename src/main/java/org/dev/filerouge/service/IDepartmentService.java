package org.dev.filerouge.service;

import org.dev.filerouge.domain.Department;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface IDepartmentService {
    Department save(Department department);

    Department update(Department department);

    Department findById(UUID id);

    void delete(UUID id);

    Page<Department> findAll(int page, int size);
}
