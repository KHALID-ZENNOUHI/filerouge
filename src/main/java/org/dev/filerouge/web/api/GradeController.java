package org.dev.filerouge.web.api;

import org.dev.filerouge.domain.Grade;
import org.dev.filerouge.service.IGradeService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing {@link Grade} entities.
 */
@RestController
@RequestMapping("/api/grades")
public class GradeController extends BaseController<Grade, IGradeService> {

    public GradeController(IGradeService gradeService) {
        super(gradeService);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setId(Grade grade, UUID id) {
        grade.setId(id);
    }

    /**
     * Finds all grades for a specific student.
     *
     * @param studentId The student ID
     * @return The list of grades
     */
    @GetMapping("/by-student/{studentId}")
    public ResponseEntity<List<Grade>> findByStudentId(@PathVariable UUID studentId) {
        List<Grade> grades = service.findByStudentId(studentId);
        return ResponseEntity.ok(grades);
    }

    /**
     * Finds all grades for a specific student with pagination.
     *
     * @param studentId The student ID
     * @param page The page number (0-indexed)
     * @param size The page size
     * @return A page of grades
     */
    @GetMapping("/by-student/{studentId}/paged")
    public ResponseEntity<Page<Grade>> findByStudentIdPaged(
            @PathVariable UUID studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Grade> grades = service.findByStudentId(studentId, page, size);
        return ResponseEntity.ok(grades);
    }

    /**
     * Finds all grades for a specific activity.
     *
     * @param activityId The activity ID
     * @return The list of grades
     */
    @GetMapping("/by-activity/{activityId}")
    public ResponseEntity<List<Grade>> findByActivityId(@PathVariable UUID activityId) {
        List<Grade> grades = service.findByActivityId(activityId);
        return ResponseEntity.ok(grades);
    }

    /**
     * Calculates the average grade for a student.
     *
     * @param studentId The student ID
     * @return The average grade, or null if the student has no grades
     */
    @GetMapping("/student/{studentId}/average")
    public ResponseEntity<Float> calculateStudentAverage(@PathVariable UUID studentId) {
        Float average = service.calculateStudentAverage(studentId);
        return ResponseEntity.ok(average);
    }

    /**
     * Calculates the average grade for an activity.
     *
     * @param activityId The activity ID
     * @return The average grade, or null if the activity has no grades
     */
    @GetMapping("/activity/{activityId}/average")
    public ResponseEntity<Float> calculateActivityAverage(@PathVariable UUID activityId) {
        Float average = service.calculateActivityAverage(activityId);
        return ResponseEntity.ok(average);
    }

    /**
     * Deletes all grades for a student.
     *
     * @param studentId The student ID
     * @return HTTP 204 No Content status
     */
    @DeleteMapping("/by-student/{studentId}")
    public ResponseEntity<Void> deleteByStudentId(@PathVariable UUID studentId) {
        service.deleteByStudentId(studentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes all grades for an activity.
     *
     * @param activityId The activity ID
     * @return HTTP 204 No Content status
     */
    @DeleteMapping("/by-activity/{activityId}")
    public ResponseEntity<Void> deleteByActivityId(@PathVariable UUID activityId) {
        service.deleteByActivityId(activityId);
        return ResponseEntity.noContent().build();
    }
}