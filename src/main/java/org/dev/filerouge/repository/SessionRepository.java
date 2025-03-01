package org.dev.filerouge.repository;

import org.dev.filerouge.domain.Session;
import org.dev.filerouge.domain.Subject;
import org.dev.filerouge.domain.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {

    /**
     * Finds all sessions for a specific teacher
     *
     * @param teacher the teacher
     * @return the list of sessions
     */
    List<Session> findByTeacher(Teacher teacher);

    /**
     * Finds all sessions for a specific teacher with pagination
     *
     * @param teacher the teacher
     * @param pageable the pagination information
     * @return a page of sessions
     */
    Page<Session> findByTeacher(Teacher teacher, Pageable pageable);

    /**
     * Finds all sessions for a specific teacher ID
     *
     * @param teacherId the teacher ID
     * @return the list of sessions
     */
    List<Session> findByTeacherId(UUID teacherId);

    /**
     * Finds all sessions for a specific subject
     *
     * @param subject the subject
     * @return the list of sessions
     */
    List<Session> findBySubject(Subject subject);

    /**
     * Finds all sessions for a specific subject with pagination
     *
     * @param subject the subject
     * @param pageable the pagination information
     * @return a page of sessions
     */
    Page<Session> findBySubject(Subject subject, Pageable pageable);

    /**
     * Finds all sessions for a specific subject ID
     *
     * @param subjectId the subject ID
     * @return the list of sessions
     */
    List<Session> findBySubjectId(UUID subjectId);

    /**
     * Finds all sessions that overlap with a given time range
     *
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @return the list of overlapping sessions
     */
    @Query("SELECT s FROM Session s WHERE " +
            "(s.startDate <= :endDate AND s.endDate >= :startDate)")
    List<Session> findOverlappingSessions(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Finds all sessions for a specific teacher that overlap with a given time range
     *
     * @param teacherId the teacher ID
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @return the list of overlapping sessions
     */
    @Query("SELECT s FROM Session s WHERE " +
            "s.teacher.id = :teacherId AND " +
            "(s.startDate <= :endDate AND s.endDate >= :startDate)")
    List<Session> findOverlappingSessionsByTeacher(UUID teacherId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Finds all sessions between two dates
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return the list of sessions
     */
    List<Session> findByStartDateGreaterThanEqualAndEndDateLessThanEqual(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Finds all sessions for a specific teacher between two dates
     *
     * @param teacherId the teacher ID
     * @param startDate the start date
     * @param endDate the end date
     * @return the list of sessions
     */
    List<Session> findByTeacherIdAndStartDateGreaterThanEqualAndEndDateLessThanEqual(
            UUID teacherId, LocalDateTime startDate, LocalDateTime endDate);

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