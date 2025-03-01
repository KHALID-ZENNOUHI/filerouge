package org.dev.filerouge.service;

import org.dev.filerouge.domain.Session;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service interface for managing {@link Session} entities.
 */
public interface ISessionService extends BaseService<Session> {

    /**
     * Finds all sessions for a specific teacher
     *
     * @param teacherId the teacher ID
     * @return the list of sessions
     */
    List<Session> findByTeacherId(UUID teacherId);

    /**
     * Finds all sessions for a specific teacher with pagination
     *
     * @param teacherId the teacher ID
     * @param page the page number (0-based)
     * @param size the page size
     * @return a page of sessions
     */
    Page<Session> findByTeacherId(UUID teacherId, int page, int size);

    /**
     * Finds all sessions for a specific subject
     *
     * @param subjectId the subject ID
     * @return the list of sessions
     */
    List<Session> findBySubjectId(UUID subjectId);

    /**
     * Finds all sessions for a specific subject with pagination
     *
     * @param subjectId the subject ID
     * @param page the page number (0-based)
     * @param size the page size
     * @return a page of sessions
     */
    Page<Session> findBySubjectId(UUID subjectId, int page, int size);

    /**
     * Finds all sessions that overlap with a given time range
     *
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @return the list of overlapping sessions
     */
    List<Session> findOverlappingSessions(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Finds all sessions for a specific teacher that overlap with a given time range
     *
     * @param teacherId the teacher ID
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @return the list of overlapping sessions
     */
    List<Session> findOverlappingSessionsByTeacher(UUID teacherId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Finds all sessions between two dates
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return the list of sessions
     */
    List<Session> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Finds all sessions for a specific teacher between two dates
     *
     * @param teacherId the teacher ID
     * @param startDate the start date
     * @param endDate the end date
     * @return the list of sessions
     */
    List<Session> findByTeacherIdAndDateRange(UUID teacherId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Gets session statistics for a specific time period
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return a map of statistics
     */
    Map<String, Object> getSessionStatistics(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Gets teacher session statistics for a specific time period
     *
     * @param teacherId the teacher ID
     * @param startDate the start date
     * @param endDate the end date
     * @return a map of statistics
     */
    Map<String, Object> getTeacherSessionStatistics(UUID teacherId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Checks if a session can be scheduled (no conflicts with existing sessions)
     *
     * @param startDate the start date
     * @param endDate the end date
     * @param teacherId the teacher ID
     * @return true if the session can be scheduled, false otherwise
     */
    boolean canScheduleSession(LocalDateTime startDate, LocalDateTime endDate, UUID teacherId);

    /**
     * Calculates the total duration of sessions for a specific teacher in a time period
     *
     * @param teacherId the teacher ID
     * @param startDate the start date
     * @param endDate the end date
     * @return the total duration in hours
     */
    double calculateTeacherTotalHours(UUID teacherId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Counts the number of sessions for a specific teacher
     *
     * @param teacherId the teacher ID
     * @return the number of sessions
     */
    long countByTeacherId(UUID teacherId);

    /**
     * Counts the number of sessions for a specific subject
     *
     * @param subjectId the subject ID
     * @return the number of sessions
     */
    long countBySubjectId(UUID subjectId);

    /**
     * Deletes all sessions for a specific teacher
     *
     * @param teacherId the teacher ID
     */
    void deleteByTeacherId(UUID teacherId);

    /**
     * Deletes all sessions for a specific subject
     *
     * @param subjectId the subject ID
     */
    void deleteBySubjectId(UUID subjectId);
}