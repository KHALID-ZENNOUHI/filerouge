package org.dev.filerouge.web.api;

import org.dev.filerouge.domain.Department;
import org.dev.filerouge.service.IDepartmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for managing {@link Department} entities.
 */
@RestController
@RequestMapping("/api/departments")
public class DepartmentController extends BaseController<Department, IDepartmentService> {

    public DepartmentController(IDepartmentService departmentService) {
        super(departmentService);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setId(Department department, UUID id) {
        department.setId(id);
    }

    /**
     * Finds a department by its name.
     *
     * @param name The name to search for
     * @return The found department
     */
    @GetMapping("/by-name/{name}")
    public ResponseEntity<Department> findByName(@PathVariable String name) {
        Department department = service.findByName(name);
        return ResponseEntity.ok(department);
    }

    /**
     * Checks if a department with the given name exists.
     *
     * @param name The name to check
     * @return true if a department with the name exists, false otherwise
     */
    @GetMapping("/exists/name/{name}")
    public ResponseEntity<Boolean> existsByName(@PathVariable String name) {
        boolean exists = service.existsByName(name);
        return ResponseEntity.ok(exists);
    }
}