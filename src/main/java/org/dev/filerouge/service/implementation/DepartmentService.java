package org.dev.filerouge.service.implementation;

import lombok.RequiredArgsConstructor;
import org.dev.filerouge.domain.Department;
import org.dev.filerouge.repository.DepartmentRepository;
import org.dev.filerouge.service.IDepartmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DepartmentService implements IDepartmentService {
    private final DepartmentRepository departmentRepository;

    @Override
    public Department save(Department department) {
        return departmentRepository.save(department);
    }

    @Override
    public Department update(Department department) {
        Optional<Department> existingDepartment = departmentRepository.findById(department.getId());
        if (existingDepartment.isEmpty()) {
            throw new IllegalArgumentException("Department not found with ID: " + department.getId());
        }
        return departmentRepository.save(department);
    }

    @Override
    public Department findById(UUID id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Department not found with ID: " + id));
    }

    @Override
    public void delete(UUID id) {
        if (!departmentRepository.existsById(id)) {
            throw new IllegalArgumentException("Department not found with ID: " + id);
        }
        departmentRepository.deleteById(id);
    }

    @Override
    public Page<Department> findAll(int page, int size) {
        return departmentRepository.findAll(PageRequest.of(page, size));
    }
}
