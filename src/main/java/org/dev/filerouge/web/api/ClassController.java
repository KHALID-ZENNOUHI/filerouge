package org.dev.filerouge.web.api;

import org.dev.filerouge.domain.Class;
import org.dev.filerouge.service.IClassService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing {@link Class} entities.
 */
@RestController
@RequestMapping("/api/classes")
public class ClassController extends BaseController<Class, IClassService> {

    public ClassController(IClassService classService) {
        super(classService);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setId(Class aClass, UUID id) {
        aClass.setId(id);
    }

    /**
     * Finds a class by its name.
     *
     * @param name The name to search for
     * @return The found class
     */
    @GetMapping("/by-name/{name}")
    public ResponseEntity<Class> findByName(@PathVariable String name) {
        Class aClass = service.findByName(name);
        return ResponseEntity.ok(aClass);
    }

    /**
     * Checks if a class with the given name exists.
     *
     * @param name The name to check
     * @return true if a class with the name exists, false otherwise
     */
    @GetMapping("/exists/name/{name}")
    public ResponseEntity<Boolean> existsByName(@PathVariable String name) {
        boolean exists = service.existsByName(name);
        return ResponseEntity.ok(exists);
    }

    /**
     * Finds all classes for a specific level.
     *
     * @param levelId The level ID
     * @return The list of classes
     */
    @GetMapping("/by-level/{levelId}")
    public ResponseEntity<List<Class>> findByLevelId(@PathVariable UUID levelId) {
        List<Class> classes = service.findByLevelId(levelId);
        return ResponseEntity.ok(classes);
    }

    /**
     * Finds all classes for a specific level with pagination.
     *
     * @param levelId The level ID
     * @param page The page number (0-indexed)
     * @param size The page size
     * @return A page of classes
     */
    @GetMapping("/by-level/{levelId}/paged")
    public ResponseEntity<Page<Class>> findByLevelIdPaged(
            @PathVariable UUID levelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Class> classes = service.findByLevelId(levelId, page, size);
        return ResponseEntity.ok(classes);
    }

    /**
     * Finds all classes for a specific department.
     *
     * @param departmentId The department ID
     * @return The list of classes
     */
    @GetMapping("/by-department/{departmentId}")
    public ResponseEntity<List<Class>> findByDepartmentId(@PathVariable UUID departmentId) {
        List<Class> classes = service.findByDepartmentId(departmentId);
        return ResponseEntity.ok(classes);
    }

    /**
     * Counts the number of classes for a specific level.
     *
     * @param levelId The level ID
     * @return The number of classes
     */
    @GetMapping("/count/by-level/{levelId}")
    public ResponseEntity<Long> countByLevelId(@PathVariable UUID levelId) {
        long count = service.countByLevelId(levelId);
        return ResponseEntity.ok(count);
    }

    /**
     * Finds all classes for a specific program.
     *
     * @param programId The program ID
     * @return The list of classes
     */
    @GetMapping("/by-program/{programId}")
    public ResponseEntity<List<Class>> findByProgramId(@PathVariable UUID programId) {
        List<Class> classes = service.findByProgramId(programId);
        return ResponseEntity.ok(classes);
    }

    /**
     * Finds all classes for a specific program with pagination.
     *
     * @param programId The program ID
     * @param page The page number (0-indexed)
     * @param size The page size
     * @return A page of classes
     */
    @GetMapping("/by-program/{programId}/paged")
    public ResponseEntity<Page<Class>> findByProgramIdPaged(
            @PathVariable UUID programId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Class> classes = service.findByProgramId(programId, page, size);
        return ResponseEntity.ok(classes);
    }

    /**
     * Finds all classes that share a program with a specific subject.
     *
     * @param subjectId The subject ID
     * @return The list of classes
     */
    @GetMapping("/by-subject/{subjectId}")
    public ResponseEntity<List<Class>> findBySubjectId(@PathVariable UUID subjectId) {
        List<Class> classes = service.findBySubjectId(subjectId);
        return ResponseEntity.ok(classes);
    }

    /**
     * Counts the number of classes for a specific program.
     *
     * @param programId The program ID
     * @return The number of classes
     */
    @GetMapping("/count/by-program/{programId}")
    public ResponseEntity<Long> countByProgramId(@PathVariable UUID programId) {
        long count = service.countByProgramId(programId);
        return ResponseEntity.ok(count);
    }

    /**
     * Assigns a class to a program.
     *
     * @param classId The class ID
     * @param programId The program ID
     * @return The updated class
     */
    @PutMapping("/{classId}/assign-to-program/{programId}")
    public ResponseEntity<Class> assignToProgram(@PathVariable UUID classId, @PathVariable UUID programId) {
        Class updatedClass = service.assignToProgram(classId, programId);
        return ResponseEntity.ok(updatedClass);
    }

    /**
     * Removes a class from its program.
     *
     * @param classId The class ID
     * @return The updated class
     */
    @PutMapping("/{classId}/remove-from-program")
    public ResponseEntity<Class> removeFromProgram(@PathVariable UUID classId) {
        Class updatedClass = service.removeFromProgram(classId);
        return ResponseEntity.ok(updatedClass);
    }
}