package org.dev.filerouge.web.api;

import org.dev.filerouge.domain.Session;
import org.dev.filerouge.service.ISessionService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for managing {@link Session} entities.
 */
@RestController
@RequestMapping("/api/sessions")
public class SessionController extends BaseController<Session, ISessionService> {

    public SessionController(ISessionService sessionService) {
        super(sessionService);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setId(Session session, UUID id) {
        session.setId(id);
    }

    /**
     * Finds all sessions for a specific teacher.
     *
     * @param teacherId The teacher ID
     * @return The list of sessions
     */
    @GetMapping("/by-teacher/{teacherId}")
    public ResponseEntity<List<Session>> findByTeacherId(@PathVariable UUID teacherId) {
        List<Session> sessions = service.findByTeacherId(teacherId);
        return ResponseEntity.ok(sessions);
    }

    /**
     * Finds all sessions for a specific teacher with pagination.
     *
     * @param teacherId The teacher ID
     * @param page The page number (0-indexed)
     * @param size The page size
     * @return A page of sessions
     */
    @GetMapping("/by-teacher/{teacherId}/paged")
    public ResponseEntity<Page<Session>> findByTeacherIdPaged(
            @PathVariable UUID teacherId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Session> sessions = service.findByTeacherId(teacherId, page, size);
        return ResponseEntity.ok(sessions);
    }

    /**
     * Finds all sessions for a specific subject.
     *
     * @param subjectId The subject ID
     * @return The list of sessions
     */
    @GetMapping("/by-subject/{subjectId}")
    public ResponseEntity<List<Session>> findBySubjectId(@PathVariable UUID subjectId) {
        List<Session> sessions = service.findBySubjectId(subjectId);
        return ResponseEntity.ok(sessions);
    }

    /**
     * Finds all sessions for a specific subject with pagination.
     *
     * @param subjectId The subject ID
     * @param page The page number (0-indexed)
     * @param size The page size
     * @return A page of sessions
     */
    @GetMapping("/by-subject/{subjectId}/paged")
    public ResponseEntity<Page<Session>> findBySubjectIdPaged(
            @PathVariable UUID subjectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Session> sessions = service.findBySubjectId(subjectId, page, size);
        return ResponseEntity.ok(sessions);
    }

    /**
     * Finds all sessions that overlap with a given time range.
     *
     * @param startDate The start date of the range
     * @param endDate The end date of the range
     * @return The list of overlapping sessions
     */
    @GetMapping("/overlapping")
    public ResponseEntity<List<Session>> findOverlappingSessions(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Session> sessions = service.findOverlappingSessions(startDate, endDate);
        return ResponseEntity.ok(sessions);
    }

    /**
     * Finds all sessions for a specific teacher that overlap with a given time range.
     *
     * @param teacherId The teacher ID
     * @param startDate The start date of the range
     * @param endDate The end date of the range
     * @return The list of overlapping sessions
     */
    @GetMapping("/overlapping/teacher/{teacherId}")
    public ResponseEntity<List<Session>> findOverlappingSessionsByTeacher(
            @PathVariable UUID teacherId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Session> sessions = service.findOverlappingSessionsByTeacher(teacherId, startDate, endDate);
        return ResponseEntity.ok(sessions);
    }

    /**
     * Finds all sessions between two dates.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return The list of sessions
     */
    @GetMapping("/by-date-range")
    public ResponseEntity<List<Session>> findByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Session> sessions = service.findByDateRange(startDate, endDate);
        return ResponseEntity.ok(sessions);
    }

    /**
     * Finds all sessions for a specific teacher between two dates.
     *
     * @param teacherId The teacher ID
     * @param startDate The start date
     * @param endDate The end date
     * @return The list of sessions
     */
    @GetMapping("/by-date-range/teacher/{teacherId}")
    public ResponseEntity<List<Session>> findByTeacherIdAndDateRange(
            @PathVariable UUID teacherId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Session> sessions = service.findByTeacherIdAndDateRange(teacherId, startDate, endDate);
        return ResponseEntity.ok(sessions);
    }

    /**
     * Gets session statistics for a specific time period.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return A map of statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getSessionStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Map<String, Object> statistics = service.getSessionStatistics(startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Gets teacher session statistics for a specific time period.
     *
     * @param teacherId The teacher ID
     * @param startDate The start date
     * @param endDate The end date
     * @return A map of statistics
     */
    @GetMapping("/statistics/teacher/{teacherId}")
    public ResponseEntity<Map<String, Object>> getTeacherSessionStatistics(
            @PathVariable UUID teacherId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Map<String, Object> statistics = service.getTeacherSessionStatistics(teacherId, startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Checks if a session can be scheduled (no conflicts with existing sessions).
     *
     * @param startDate The start date
     * @param endDate The end date
     * @param teacherId The teacher ID
     * @return true if the session can be scheduled, false otherwise
     */
    @GetMapping("/can-schedule")
    public ResponseEntity<Boolean> canScheduleSession(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam UUID teacherId) {
        boolean canSchedule = service.canScheduleSession(startDate, endDate, teacherId);
        return ResponseEntity.ok(canSchedule);
    }

    /**
     * Calculates the total duration of sessions for a specific teacher in a time period.
     *
     * @param teacherId The teacher ID
     * @param startDate The start date
     * @param endDate The end date
     * @return The total duration in hours
     */
    @GetMapping("/teacher/{teacherId}/total-hours")
    public ResponseEntity<Double> calculateTeacherTotalHours(
            @PathVariable UUID teacherId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        double totalHours = service.calculateTeacherTotalHours(teacherId, startDate, endDate);
        return ResponseEntity.ok(totalHours);
    }

    /**
     * Counts the number of sessions for a specific teacher.
     *
     * @param teacherId The teacher ID
     * @return The number of sessions
     */
    @GetMapping("/count/by-teacher/{teacherId}")
    public ResponseEntity<Long> countByTeacherId(@PathVariable UUID teacherId) {
        long count = service.countByTeacherId(teacherId);
        return ResponseEntity.ok(count);
    }

    /**
     * Counts the number of sessions for a specific subject.
     *
     * @param subjectId The subject ID
     * @return The number of sessions
     */
    @GetMapping("/count/by-subject/{subjectId}")
    public ResponseEntity<Long> countBySubjectId(@PathVariable UUID subjectId) {
        long count = service.countBySubjectId(subjectId);
        return ResponseEntity.ok(count);
    }

    /**
     * Deletes all sessions for a specific teacher.
     *
     * @param teacherId The teacher ID
     * @return HTTP 204 No Content status
     */
    @DeleteMapping("/by-teacher/{teacherId}")
    public ResponseEntity<Void> deleteByTeacherId(@PathVariable UUID teacherId) {
        service.deleteByTeacherId(teacherId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes all sessions for a specific subject.
     *
     * @param subjectId The subject ID
     * @return HTTP 204 No Content status
     */
    @DeleteMapping("/by-subject/{subjectId}")
    public ResponseEntity<Void> deleteBySubjectId(@PathVariable UUID subjectId) {
        service.deleteBySubjectId(subjectId);
        return ResponseEntity.noContent().build();
    }
}