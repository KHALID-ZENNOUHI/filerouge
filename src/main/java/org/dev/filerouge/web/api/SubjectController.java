package org.dev.filerouge.web.api;

import org.dev.filerouge.domain.Subject;
import org.dev.filerouge.service.ISubjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing {@link Subject} entities.
 */
@RestController
@RequestMapping("/api/subjects")
public class SubjectController extends BaseController<Subject, ISubjectService> {

    public SubjectController(ISubjectService subjectService) {
        super(subjectService);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setId(Subject subject, UUID id) {
        subject.setId(id);
    }

    /**
     * Finds a subject by its name.
     *
     * @param name The name to search for
     * @return The found subject
     */
    @GetMapping("/by-name/{name}")
    public ResponseEntity<Subject> findByName(@PathVariable String name) {
        Subject subject = service.findByName(name);
        return ResponseEntity.ok(subject);
    }

    /**
     * Checks if a subject with the given name exists.
     *
     * @param name The name to check
     * @return true if a subject with the name exists, false otherwise
     */
    @GetMapping("/exists/name/{name}")
    public ResponseEntity<Boolean> existsByName(@PathVariable String name) {
        boolean exists = service.existsByName(name);
        return ResponseEntity.ok(exists);
    }

    /**
     * Finds all subjects associated with a specific class.
     *
     * @param classId The class ID
     * @return The list of subjects
     */
    @GetMapping("/by-class/{classId}")
    public ResponseEntity<List<Subject>> findByClassId(@PathVariable UUID classId) {
        List<Subject> subjects = service.findByClassId(classId);
        return ResponseEntity.ok(subjects);
    }

    /**
     * Finds all subjects taught by a specific teacher.
     *
     * @param teacherId The teacher ID
     * @return The list of subjects
     */
    @GetMapping("/by-teacher/{teacherId}")
    public ResponseEntity<List<Subject>> findByTeacherId(@PathVariable UUID teacherId) {
        List<Subject> subjects = service.findByTeacherId(teacherId);
        return ResponseEntity.ok(subjects);
    }

    /**
     * Searches for subjects by name.
     *
     * @param searchTerm The search term
     * @return The list of matching subjects
     */
    @GetMapping("/search")
    public ResponseEntity<List<Subject>> searchByName(@RequestParam String searchTerm) {
        List<Subject> subjects = service.searchByName(searchTerm);
        return ResponseEntity.ok(subjects);
    }
}