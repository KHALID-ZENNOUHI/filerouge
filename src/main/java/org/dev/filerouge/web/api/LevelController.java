package org.dev.filerouge.web.api;

import org.dev.filerouge.domain.Level;
import org.dev.filerouge.service.ILevelService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing {@link Level} entities.
 */
@RestController
@RequestMapping("/api/levels")
public class LevelController extends BaseController<Level, ILevelService> {

    public LevelController(ILevelService levelService) {
        super(levelService);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setId(Level level, UUID id) {
        level.setId(id);
    }

    /**
     * Finds a level by its name.
     *
     * @param name The name to search for
     * @return The found level
     */
    @GetMapping("/by-name/{name}")
    public ResponseEntity<Level> findByName(@PathVariable String name) {
        Level level = service.findByName(name);
        return ResponseEntity.ok(level);
    }

    /**
     * Finds all levels for a specific department.
     *
     * @param departmentId The department ID
     * @return The list of levels
     */
    @GetMapping("/by-department/{departmentId}")
    public ResponseEntity<List<Level>> findByDepartmentId(@PathVariable UUID departmentId) {
        List<Level> levels = service.findByDepartmentId(departmentId);
        return ResponseEntity.ok(levels);
    }

    /**
     * Finds all levels for a specific department with pagination.
     *
     * @param departmentId The department ID
     * @param page The page number (0-indexed)
     * @param size The page size
     * @return A page of levels
     */
    @GetMapping("/by-department/{departmentId}/paged")
    public ResponseEntity<Page<Level>> findByDepartmentIdPaged(
            @PathVariable UUID departmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Level> levels = service.findByDepartmentId(departmentId, page, size);
        return ResponseEntity.ok(levels);
    }

    /**
     * Checks if a level with the given name exists.
     *
     * @param name The name to check
     * @return true if a level with the name exists, false otherwise
     */
    @GetMapping("/exists/name/{name}")
    public ResponseEntity<Boolean> existsByName(@PathVariable String name) {
        boolean exists = service.existsByName(name);
        return ResponseEntity.ok(exists);
    }

    /**
     * Checks if a level with the given name exists in a specific department.
     *
     * @param name The name to check
     * @param departmentId The department ID
     * @return true if a level with the name exists in the department, false otherwise
     */
    @GetMapping("/exists/name/{name}/department/{departmentId}")
    public ResponseEntity<Boolean> existsByNameAndDepartmentId(
            @PathVariable String name,
            @PathVariable UUID departmentId) {
        boolean exists = service.existsByNameAndDepartmentId(name, departmentId);
        return ResponseEntity.ok(exists);
    }

    /**
     * Counts the number of levels in a specific department.
     *
     * @param departmentId The department ID
     * @return The number of levels
     */
    @GetMapping("/count/by-department/{departmentId}")
    public ResponseEntity<Long> countByDepartmentId(@PathVariable UUID departmentId) {
        long count = service.countByDepartmentId(departmentId);
        return ResponseEntity.ok(count);
    }

    /**
     * Searches for levels by name.
     *
     * @param searchTerm The search term
     * @return The list of matching levels
     */
    @GetMapping("/search")
    public ResponseEntity<List<Level>> searchByName(@RequestParam String searchTerm) {
        List<Level> levels = service.searchByName(searchTerm);
        return ResponseEntity.ok(levels);
    }

    /**
     * Counts the number of classes in a specific level.
     *
     * @param levelId The level ID
     * @return The number of classes
     */
    @GetMapping("/{levelId}/class-count")
    public ResponseEntity<Long> countClassesByLevelId(@PathVariable UUID levelId) {
        long count = service.countClassesByLevelId(levelId);
        return ResponseEntity.ok(count);
    }

    /**
     * Gets the hierarchy path of a level.
     *
     * @param levelId The level ID
     * @return The hierarchy path
     */
    @GetMapping("/{levelId}/hierarchy-path")
    public ResponseEntity<String> getLevelHierarchyPath(@PathVariable UUID levelId) {
        String path = service.getLevelHierarchyPath(levelId);
        return ResponseEntity.ok(path);
    }
}