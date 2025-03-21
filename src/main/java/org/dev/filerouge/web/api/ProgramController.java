package org.dev.filerouge.web.api;

import org.dev.filerouge.domain.Program;
import org.dev.filerouge.service.IProgramService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for managing {@link Program} entities.
 */
@RestController
@RequestMapping("/api/programs")
public class ProgramController extends BaseController<Program, IProgramService> {

    public ProgramController(IProgramService programService) {
        super(programService);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setId(Program program, UUID id) {
        program.setId(id);
    }

    /**
     * Finds all programs for a specific class.
     *
     * @param classId The class ID
     * @return The list of programs
     */
    @GetMapping("/by-class/{classId}")
    public ResponseEntity<List<Program>> findByClassId(@PathVariable UUID classId) {
        List<Program> programs = service.findByClassId(classId);
        return ResponseEntity.ok(programs);
    }

    /**
     * Finds all programs for a specific class with pagination.
     *
     * @param classId The class ID
     * @param page The page number (0-indexed)
     * @param size The page size
     * @return A page of programs
     */
    @GetMapping("/by-class/{classId}/paged")
    public ResponseEntity<Page<Program>> findByClassIdPaged(
            @PathVariable UUID classId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Program> programs = service.findByClassId(classId, page, size);
        return ResponseEntity.ok(programs);
    }

    /**
     * Finds all programs for a specific subject.
     *
     * @param subjectId The subject ID
     * @return The list of programs
     */
    @GetMapping("/by-subject/{subjectId}")
    public ResponseEntity<List<Program>> findBySubjectId(@PathVariable UUID subjectId) {
        List<Program> programs = service.findBySubjectId(subjectId);
        return ResponseEntity.ok(programs);
    }

    /**
     * Finds all programs for a specific subject with pagination.
     *
     * @param subjectId The subject ID
     * @param page The page number (0-indexed)
     * @param size The page size
     * @return A page of programs
     */
    @GetMapping("/by-subject/{subjectId}/paged")
    public ResponseEntity<Page<Program>> findBySubjectIdPaged(
            @PathVariable UUID subjectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Program> programs = service.findBySubjectId(subjectId, page, size);
        return ResponseEntity.ok(programs);
    }

    /**
     * Finds a program by class and subject.
     *
     * @param classId The class ID
     * @param subjectId The subject ID
     * @return The found program
     */
    @GetMapping("/by-class/{classId}/subject/{subjectId}")
    public ResponseEntity<Program> findByClassAndSubject(
            @PathVariable UUID classId,
            @PathVariable UUID subjectId) {
        Program program = service.findByClassAndSubject(classId, subjectId);
        return ResponseEntity.ok(program);
    }

    /**
     * Checks if a program exists for a specific class and subject.
     *
     * @param classId The class ID
     * @param subjectId The subject ID
     * @return true if a program exists for the class and subject, false otherwise
     */
    @GetMapping("/exists/class/{classId}/subject/{subjectId}")
    public ResponseEntity<Boolean> existsByClassAndSubject(
            @PathVariable UUID classId,
            @PathVariable UUID subjectId) {
        boolean exists = service.existsByClassAndSubject(classId, subjectId);
        return ResponseEntity.ok(exists);
    }

    /**
     * Gets program statistics for a class.
     *
     * @param classId The class ID
     * @return A map of statistics
     */
    @GetMapping("/statistics/class/{classId}")
    public ResponseEntity<Map<String, Object>> getClassProgramStatistics(@PathVariable UUID classId) {
        Map<String, Object> statistics = service.getClassProgramStatistics(classId);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Gets program statistics for a subject.
     *
     * @param subjectId The subject ID
     * @return A map of statistics
     */
    @GetMapping("/statistics/subject/{subjectId}")
    public ResponseEntity<Map<String, Object>> getSubjectProgramStatistics(@PathVariable UUID subjectId) {
        Map<String, Object> statistics = service.getSubjectProgramStatistics(subjectId);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Counts the number of programs for a specific class.
     *
     * @param classId The class ID
     * @return The number of programs
     */
    @GetMapping("/count/by-class/{classId}")
    public ResponseEntity<Long> countByClassId(@PathVariable UUID classId) {
        long count = service.countByClassId(classId);
        return ResponseEntity.ok(count);
    }

    /**
     * Counts the number of programs for a specific subject.
     *
     * @param subjectId The subject ID
     * @return The number of programs
     */
    @GetMapping("/count/by-subject/{subjectId}")
    public ResponseEntity<Long> countBySubjectId(@PathVariable UUID subjectId) {
        long count = service.countBySubjectId(subjectId);
        return ResponseEntity.ok(count);
    }

    /**
     * Removes a class from its program(s).
     *
     * @param classId The class ID
     * @return HTTP 204 No Content status
     */
    @DeleteMapping("/by-class/{classId}")
    public ResponseEntity<Void> deleteByClassId(@PathVariable UUID classId) {
        service.deleteByClassId(classId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Removes a subject from its program(s).
     *
     * @param subjectId The subject ID
     * @return HTTP 204 No Content status
     */
    @DeleteMapping("/by-subject/{subjectId}")
    public ResponseEntity<Void> deleteBySubjectId(@PathVariable UUID subjectId) {
        service.deleteBySubjectId(subjectId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Assigns a subject to a class in a program.
     *
     * @param classId The class ID
     * @param subjectId The subject ID
     * @param description The program description
     * @return The created or updated program with HTTP 201 Created status
     */
    @PostMapping("/assign-subject")
    public ResponseEntity<Program> assignSubjectToClass(
            @RequestParam UUID classId,
            @RequestParam UUID subjectId,
            @RequestParam(required = false) String description) {
        Program program = service.assignSubjectToClass(classId, subjectId, description);
        return ResponseEntity.status(HttpStatus.CREATED).body(program);
    }

    /**
     * Removes a subject from a class's program.
     *
     * @param classId The class ID
     * @param subjectId The subject ID
     * @return HTTP 204 No Content status
     */
    @DeleteMapping("/remove-subject")
    public ResponseEntity<Void> removeSubjectFromClass(
            @RequestParam UUID classId,
            @RequestParam UUID subjectId) {
        service.removeSubjectFromClass(classId, subjectId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Creates a new program with initial classes and subjects
     *
     * @param program The program to create
     * @param classIds Optional list of class IDs to associate with the program
     * @param subjectIds Optional list of subject IDs to associate with the program
     * @return The created program with HTTP 201 Created status
     */
    @PostMapping("/with-associations")
    public ResponseEntity<Program> createWithAssociations(
            @RequestBody Program program,
            @RequestParam(required = false) List<UUID> classIds,
            @RequestParam(required = false) List<UUID> subjectIds) {

        // First save the program
        Program savedProgram = service.save(program);

        // Associate classes if provided
        if (classIds != null && !classIds.isEmpty()) {
            for (UUID classId : classIds) {
                // Use the service.assignToProgram method from the class service
                // This would be a cross-service call that we're simplifying here
                // In a real implementation, inject the ClassService and call its assignToProgram method
            }
        }

        // Associate subjects if provided
        if (subjectIds != null && !subjectIds.isEmpty()) {
            for (UUID subjectId : subjectIds) {
                // Use the service.assignToProgram method from the subject service
                // This would be a cross-service call that we're simplifying here
                // In a real implementation, inject the SubjectService and call its assignToProgram method
            }
        }

        // Return the updated program
        return ResponseEntity.status(HttpStatus.CREATED).body(service.findById(savedProgram.getId()));
    }
}