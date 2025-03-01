package org.dev.filerouge.service.implementation;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.dev.filerouge.domain.Department;
import org.dev.filerouge.repository.DepartmentRepository;
import org.dev.filerouge.service.IDepartmentService;

import org.dev.filerouge.web.error.ServiceException;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Implementation of {@link IDepartmentService} for managing {@link Department} entities.
 */
@Service
@Transactional
@Slf4j
public class DepartmentService extends BaseServiceImpl<Department, DepartmentRepository> implements IDepartmentService {

    public DepartmentService(DepartmentRepository departmentRepository) {
        super(departmentRepository, "Department");
    }

    @Override
    public Department save(Department department) {
        log.debug("Saving department: {}", department);

        // Validate name uniqueness
        if (existsByName(department.getName())) {
            throw new ServiceException.DuplicateResourceException("Department", "name", department.getName());
        }

        // Validate not null/blank fields (as a secondary validation after @NotBlank)
        validateDepartment(department);

        return super.save(department);
    }

    @Override
    public Department update(Department department) {
        log.debug("Updating department: {}", department);

        // Check if department exists
        if (!existsById(department.getId())) {
            throw new ServiceException.ResourceNotFoundException("Department", "id", department.getId());
        }

        // Validate name uniqueness (only if name is changed)
        Department existingDepartment = findById(department.getId());
        if (!existingDepartment.getName().equals(department.getName()) && existsByName(department.getName())) {
            throw new ServiceException.DuplicateResourceException("Department", "name", department.getName());
        }

        // Validate not null/blank fields
        validateDepartment(department);

        return super.update(department);
    }

    @Override
    public Department findByName(String name) {
        log.debug("Finding department by name: {}", name);
        return repository.findByName(name)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("Department", "name", name));
    }

    @Override
    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }

    /**
     * Validates department fields
     *
     * @param department the department to validate
     * @throws ServiceException.ValidationException if validation fails
     */
    private void validateDepartment(Department department) {
        if (department.getName() == null || department.getName().trim().isEmpty()) {
            throw new ServiceException.ValidationException("Department name cannot be empty");
        }
    }
}