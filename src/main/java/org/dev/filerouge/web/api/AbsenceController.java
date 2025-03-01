package org.dev.filerouge.web.api;

import org.dev.filerouge.domain.Absence;
import org.dev.filerouge.domain.Enum.AbsenceStatus;
import org.dev.filerouge.service.IAbsenceService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for managing {@link Absence} entities.
 */
@RestController
@RequestMapping("/api/absences")
public class AbsenceController extends BaseController<Absence, IAbsenceService> {

    public AbsenceController(IAbsenceService absenceService) {
        super(absenceService);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setId(Absence absence, UUID id) {
        absence.setId(id);
    }

    /**
     * Finds all absences for a specific student.
     *
     * @param studentId The student ID
     * @return The list of absences
     */
    @GetMapping("/by-student/{studentId}")
    public ResponseEntity<List<Absence>> findByStudentId(@PathVariable UUID studentId) {
        List<Absence> absences = service.findByStudentId(studentId);
        return ResponseEntity.ok(absences);
    }

    /**
     * Finds all absences for a specific student with pagination.
     *
     * @param studentId The student ID
     * @param page The page number (0-indexed)
     * @param size The page size
     * @return A page of absences
     */
    @GetMapping("/by-student/{studentId}/paged")
    public ResponseEntity<Page<Absence>> findByStudentIdPaged(
            @PathVariable UUID studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Absence> absences = service.findByStudentId(studentId, page, size);
        return ResponseEntity.ok(absences);
    }

    /**
     * Finds all absences for a specific class.
     *
     * @param classId The class ID
     * @return The list of absences
     */
    @GetMapping("/by-class/{classId}")
    public ResponseEntity<List<Absence>> findByClassId(@PathVariable UUID classId) {
        List<Absence> absences = service.findByClassId(classId);
        return ResponseEntity.ok(absences);
    }

    /**
     * Finds all absences for a specific class with pagination.
     *
     * @param classId The class ID
     * @param page The page number (0-indexed)
     * @param size The page size
     * @return A page of absences
     */
    @GetMapping("/by-class/{classId}/paged")
    public ResponseEntity<Page<Absence>> findByClassIdPaged(
            @PathVariable UUID classId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Absence> absences = service.findByClassId(classId, page, size);
        return ResponseEntity.ok(absences);
    }

    /**
     * Finds all absences with a specific status.
     *
     * @param status The absence status
     * @return The list of absences
     */
    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<Absence>> findByStatus(@PathVariable AbsenceStatus status) {
        List<Absence> absences = service.findByStatus(status);
        return ResponseEntity.ok(absences);
    }

    /**
     * Finds all absences with a specific status for a student.
     *
     * @param status The absence status
     * @param studentId The student ID
     * @return The list of absences
     */
    @GetMapping("/by-status/{status}/student/{studentId}")
    public ResponseEntity<List<Absence>> findByStatusAndStudentId(
            @PathVariable AbsenceStatus status,
            @PathVariable UUID studentId) {
        List<Absence> absences = service.findByStatusAndStudentId(status, studentId);
        return ResponseEntity.ok(absences);
    }

    /**
     * Finds all absences between two dates.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return The list of absences
     */
    @GetMapping("/by-date-range")
    public ResponseEntity<List<Absence>> findByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Absence> absences = service.findByDateRange(startDate, endDate);
        return ResponseEntity.ok(absences);
    }

    /**
     * Finds all absences between two dates for a specific student.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @param studentId The student ID
     * @return The list of absences
     */
    @GetMapping("/by-date-range/student/{studentId}")
    public ResponseEntity<List<Absence>> findByDateRangeAndStudentId(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @PathVariable UUID studentId) {
        List<Absence> absences = service.findByDateRangeAndStudentId(startDate, endDate, studentId);
        return ResponseEntity.ok(absences);
    }

    /**
     * Finds all justified absences.
     *
     * @return The list of justified absences
     */
    @GetMapping("/justified")
    public ResponseEntity<List<Absence>> findJustifiedAbsences() {
        List<Absence> absences = service.findJustifiedAbsences();
        return ResponseEntity.ok(absences);
    }

    /**
     * Finds all unjustified absences.
     *
     * @return The list of unjustified absences
     */
    @GetMapping("/unjustified")
    public ResponseEntity<List<Absence>> findUnjustifiedAbsences() {
        List<Absence> absences = service.findUnjustifiedAbsences();
        return ResponseEntity.ok(absences);
    }

    /**
     * Justifies an absence.
     *
     * @param absenceId The absence ID
     * @param justificationText The justification text
     * @return The updated absence
     */
    @PutMapping("/{absenceId}/justify")
    public ResponseEntity<Absence> justifyAbsence(
            @PathVariable UUID absenceId,
            @RequestParam String justificationText) {
        Absence absence = service.justifyAbsence(absenceId, justificationText);
        return ResponseEntity.ok(absence);
    }

    /**
     * Marks an absence as unjustified.
     *
     * @param absenceId The absence ID
     * @return The updated absence
     */
    @PutMapping("/{absenceId}/mark-unjustified")
    public ResponseEntity<Absence> markAsUnjustified(@PathVariable UUID absenceId) {
        Absence absence = service.markAsUnjustified(absenceId);
        return ResponseEntity.ok(absence);
    }

    /**
     * Changes the status of an absence.
     *
     * @param absenceId The absence ID
     * @param status The new status
     * @return The updated absence
     */
    @PutMapping("/{absenceId}/change-status")
    public ResponseEntity<Absence> changeStatus(
            @PathVariable UUID absenceId,
            @RequestParam AbsenceStatus status) {
        Absence absence = service.changeStatus(absenceId, status);
        return ResponseEntity.ok(absence);
    }

    /**
     * Gets absence statistics for a student.
     *
     * @param studentId The student ID
     * @return A map of statistics
     */
    @GetMapping("/statistics/student/{studentId}")
    public ResponseEntity<Map<String, Object>> getStudentAbsenceStatistics(@PathVariable UUID studentId) {
        Map<String, Object> statistics = service.getStudentAbsenceStatistics(studentId);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Gets absence statistics for a class.
     *
     * @param classId The class ID
     * @return A map of statistics
     */
    @GetMapping("/statistics/class/{classId}")
    public ResponseEntity<Map<String, Object>> getClassAbsenceStatistics(@PathVariable UUID classId) {
        Map<String, Object> statistics = service.getClassAbsenceStatistics(classId);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Counts the number of absences for a specific student.
     *
     * @param studentId The student ID
     * @return The number of absences
     */
    @GetMapping("/count/by-student/{studentId}")
    public ResponseEntity<Long> countByStudentId(@PathVariable UUID studentId) {
        long count = service.countByStudentId(studentId);
        return ResponseEntity.ok(count);
    }

    /**
     * Counts the number of justified absences for a specific student.
     *
     * @param studentId The student ID
     * @return The number of justified absences
     */
    @GetMapping("/count/justified/by-student/{studentId}")
    public ResponseEntity<Long> countJustifiedByStudentId(@PathVariable UUID studentId) {
        long count = service.countJustifiedByStudentId(studentId);
        return ResponseEntity.ok(count);
    }

    /**
     * Counts the number of unjustified absences for a specific student.
     *
     * @param studentId The student ID
     * @return The number of unjustified absences
     */
    @GetMapping("/count/unjustified/by-student/{studentId}")
    public ResponseEntity<Long> countUnjustifiedByStudentId(@PathVariable UUID studentId) {
        long count = service.countUnjustifiedByStudentId(studentId);
        return ResponseEntity.ok(count);
    }

    /**
     * Deletes all absences for a specific student.
     *
     * @param studentId The student ID
     * @return HTTP 204 No Content status
     */
    @DeleteMapping("/by-student/{studentId}")
    public ResponseEntity<Void> deleteByStudentId(@PathVariable UUID studentId) {
        service.deleteByStudentId(studentId);
        return ResponseEntity.noContent().build();
    }
}