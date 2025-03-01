package org.dev.filerouge.service.implementation;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.dev.filerouge.domain.Session;
import org.dev.filerouge.domain.Subject;
import org.dev.filerouge.domain.Teacher;
import org.dev.filerouge.web.error.ServiceException;
import org.dev.filerouge.repository.SessionRepository;
import org.dev.filerouge.repository.SubjectRepository;
import org.dev.filerouge.repository.TeacherRepository;
import org.dev.filerouge.service.ISessionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of {@link ISessionService} for managing {@link Session} entities.
 */
@Service
@Transactional
@Slf4j
public class SessionService extends BaseServiceImpl<Session, SessionRepository> implements ISessionService {

    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;

    public SessionService(SessionRepository sessionRepository,
                          TeacherRepository teacherRepository,
                          SubjectRepository subjectRepository) {
        super(sessionRepository, "Session");
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
    }

    @Override
    public Session save(Session session) {
        log.debug("Saving session: {}", session);

        // Validate session
        validateSession(session);

        // Validate teacher existence
        if (session.getTeacher() != null && session.getTeacher().getId() != null) {
            UUID teacherId = session.getTeacher().getId();
            if (!teacherRepository.existsById(teacherId)) {
                throw new ServiceException.ResourceNotFoundException("Teacher", "id", teacherId);
            }

            // Check for scheduling conflicts
            if (!canScheduleSession(session.getStartDate(), session.getEndDate(), teacherId)) {
                throw new ServiceException.ValidationException(
                        "Teacher already has a session scheduled during this time period"
                );
            }
        } else {
            throw new ServiceException.ValidationException("Teacher is required for a session");
        }

        // Validate subject existence
        if (session.getSubject() != null && session.getSubject().getId() != null) {
            UUID subjectId = session.getSubject().getId();
            if (!subjectRepository.existsById(subjectId)) {
                throw new ServiceException.ResourceNotFoundException("Subject", "id", subjectId);
            }
        } else {
            throw new ServiceException.ValidationException("Subject is required for a session");
        }

        return super.save(session);
    }

    @Override
    public Session update(Session session) {
        log.debug("Updating session: {}", session);

        // Check if session exists
        if (!existsById(session.getId())) {
            throw new ServiceException.ResourceNotFoundException("Session", "id", session.getId());
        }

        // Validate session
        validateSession(session);

        // Get existing session to check if dates or teacher has changed
        Session existingSession = findById(session.getId());
        boolean datesChanged = !existingSession.getStartDate().equals(session.getStartDate()) ||
                !existingSession.getEndDate().equals(session.getEndDate());
        boolean teacherChanged = !existingSession.getTeacher().getId().equals(session.getTeacher().getId());

        // Validate teacher existence
        if (session.getTeacher() != null && session.getTeacher().getId() != null) {
            UUID teacherId = session.getTeacher().getId();
            if (!teacherRepository.existsById(teacherId)) {
                throw new ServiceException.ResourceNotFoundException("Teacher", "id", teacherId);
            }

            // Check for scheduling conflicts (only if dates or teacher has changed)
            if ((datesChanged || teacherChanged) &&
                    !canScheduleSession(session.getStartDate(), session.getEndDate(), teacherId, session.getId())) {
                throw new ServiceException.ValidationException(
                        "Teacher already has a session scheduled during this time period"
                );
            }
        } else {
            throw new ServiceException.ValidationException("Teacher is required for a session");
        }

        // Validate subject existence
        if (session.getSubject() != null && session.getSubject().getId() != null) {
            UUID subjectId = session.getSubject().getId();
            if (!subjectRepository.existsById(subjectId)) {
                throw new ServiceException.ResourceNotFoundException("Subject", "id", subjectId);
            }
        } else {
            throw new ServiceException.ValidationException("Subject is required for a session");
        }

        return super.update(session);
    }

    @Override
    public List<Session> findByTeacherId(UUID teacherId) {
        log.debug("Finding sessions by teacher ID: {}", teacherId);

        // Validate teacher existence
        if (!teacherRepository.existsById(teacherId)) {
            throw new ServiceException.ResourceNotFoundException("Teacher", "id", teacherId);
        }

        return repository.findByTeacherId(teacherId);
    }

    @Override
    public Page<Session> findByTeacherId(UUID teacherId, int page, int size) {
        log.debug("Finding sessions by teacher ID with pagination: {}", teacherId);

        // Validate teacher existence
        if (!teacherRepository.existsById(teacherId)) {
            throw new ServiceException.ResourceNotFoundException("Teacher", "id", teacherId);
        }

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("Teacher", "id", teacherId));

        return repository.findByTeacher(teacher, PageRequest.of(page, size));
    }

    @Override
    public List<Session> findBySubjectId(UUID subjectId) {
        log.debug("Finding sessions by subject ID: {}", subjectId);

        // Validate subject existence
        if (!subjectRepository.existsById(subjectId)) {
            throw new ServiceException.ResourceNotFoundException("Subject", "id", subjectId);
        }

        return repository.findBySubjectId(subjectId);
    }

    @Override
    public Page<Session> findBySubjectId(UUID subjectId, int page, int size) {
        log.debug("Finding sessions by subject ID with pagination: {}", subjectId);

        // Validate subject existence
        if (!subjectRepository.existsById(subjectId)) {
            throw new ServiceException.ResourceNotFoundException("Subject", "id", subjectId);
        }

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("Subject", "id", subjectId));

        return repository.findBySubject(subject, PageRequest.of(page, size));
    }

    @Override
    public List<Session> findOverlappingSessions(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Finding overlapping sessions between {} and {}", startDate, endDate);

        if (startDate == null || endDate == null) {
            throw new ServiceException.ValidationException("Start date and end date cannot be null");
        }

        if (startDate.isAfter(endDate)) {
            throw new ServiceException.ValidationException("Start date cannot be after end date");
        }

        return repository.findOverlappingSessions(startDate, endDate);
    }

    @Override
    public List<Session> findOverlappingSessionsByTeacher(UUID teacherId, LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Finding overlapping sessions for teacher ID {} between {} and {}", teacherId, startDate, endDate);

        // Validate teacher existence
        if (!teacherRepository.existsById(teacherId)) {
            throw new ServiceException.ResourceNotFoundException("Teacher", "id", teacherId);
        }

        if (startDate == null || endDate == null) {
            throw new ServiceException.ValidationException("Start date and end date cannot be null");
        }

        if (startDate.isAfter(endDate)) {
            throw new ServiceException.ValidationException("Start date cannot be after end date");
        }

        return repository.findOverlappingSessionsByTeacher(teacherId, startDate, endDate);
    }

    @Override
    public List<Session> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Finding sessions between {} and {}", startDate, endDate);

        if (startDate == null || endDate == null) {
            throw new ServiceException.ValidationException("Start date and end date cannot be null");
        }

        if (startDate.isAfter(endDate)) {
            throw new ServiceException.ValidationException("Start date cannot be after end date");
        }

        return repository.findByStartDateGreaterThanEqualAndEndDateLessThanEqual(startDate, endDate);
    }

    @Override
    public List<Session> findByTeacherIdAndDateRange(UUID teacherId, LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Finding sessions for teacher ID {} between {} and {}", teacherId, startDate, endDate);

        // Validate teacher existence
        if (!teacherRepository.existsById(teacherId)) {
            throw new ServiceException.ResourceNotFoundException("Teacher", "id", teacherId);
        }

        if (startDate == null || endDate == null) {
            throw new ServiceException.ValidationException("Start date and end date cannot be null");
        }

        if (startDate.isAfter(endDate)) {
            throw new ServiceException.ValidationException("Start date cannot be after end date");
        }

        return repository.findByTeacherIdAndStartDateGreaterThanEqualAndEndDateLessThanEqual(
                teacherId, startDate, endDate);
    }

    @Override
    public Map<String, Object> getSessionStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Getting session statistics between {} and {}", startDate, endDate);

        if (startDate == null || endDate == null) {
            throw new ServiceException.ValidationException("Start date and end date cannot be null");
        }

        if (startDate.isAfter(endDate)) {
            throw new ServiceException.ValidationException("Start date cannot be after end date");
        }

        Map<String, Object> statistics = new HashMap<>();

        List<Session> sessions = findByDateRange(startDate, endDate);

        // Total number of sessions
        statistics.put("totalSessions", sessions.size());

        // Total duration in hours
        double totalHours = sessions.stream()
                .mapToDouble(session ->
                        Duration.between(session.getStartDate(), session.getEndDate()).toMinutes() / 60.0)
                .sum();
        statistics.put("totalHours", Math.round(totalHours * 100.0) / 100.0);

        // Sessions by subject
        Map<String, Long> sessionsBySubject = sessions.stream()
                .collect(Collectors.groupingBy(
                        session -> session.getSubject().getName(),
                        Collectors.counting()
                ));
        statistics.put("sessionsBySubject", sessionsBySubject);

        // Sessions by teacher
        Map<String, Long> sessionsByTeacher = sessions.stream()
                .collect(Collectors.groupingBy(
                        session -> session.getTeacher().getFirstName() + " " + session.getTeacher().getLastName(),
                        Collectors.counting()
                ));
        statistics.put("sessionsByTeacher", sessionsByTeacher);

        // Average session duration in hours
        double avgDuration = sessions.isEmpty() ? 0 : totalHours / sessions.size();
        statistics.put("averageSessionDuration", Math.round(avgDuration * 100.0) / 100.0);

        return statistics;
    }

    @Override
    public Map<String, Object> getTeacherSessionStatistics(UUID teacherId, LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Getting session statistics for teacher ID {} between {} and {}", teacherId, startDate, endDate);

        // Validate teacher existence
        if (!teacherRepository.existsById(teacherId)) {
            throw new ServiceException.ResourceNotFoundException("Teacher", "id", teacherId);
        }

        if (startDate == null || endDate == null) {
            throw new ServiceException.ValidationException("Start date and end date cannot be null");
        }

        if (startDate.isAfter(endDate)) {
            throw new ServiceException.ValidationException("Start date cannot be after end date");
        }

        Map<String, Object> statistics = new HashMap<>();

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("Teacher", "id", teacherId));

        statistics.put("teacherName", teacher.getFirstName() + " " + teacher.getLastName());

        List<Session> sessions = findByTeacherIdAndDateRange(teacherId, startDate, endDate);

        // Total number of sessions
        statistics.put("totalSessions", sessions.size());

        // Total teaching hours
        double totalHours = calculateTeacherTotalHours(teacherId, startDate, endDate);
        statistics.put("totalHours", Math.round(totalHours * 100.0) / 100.0);

        // Sessions by subject
        Map<String, Long> sessionsBySubject = sessions.stream()
                .collect(Collectors.groupingBy(
                        session -> session.getSubject().getName(),
                        Collectors.counting()
                ));
        statistics.put("sessionsBySubject", sessionsBySubject);

        // Hours by subject
        Map<String, Double> hoursBySubject = sessions.stream()
                .collect(Collectors.groupingBy(
                        session -> session.getSubject().getName(),
                        Collectors.summingDouble(session ->
                                Duration.between(session.getStartDate(), session.getEndDate()).toMinutes() / 60.0)
                ));
        // Round the values
        hoursBySubject.replaceAll((k, v) -> Math.round(v * 100.0) / 100.0);
        statistics.put("hoursBySubject", hoursBySubject);

        // Average session duration in hours
        double avgDuration = sessions.isEmpty() ? 0 : totalHours / sessions.size();
        statistics.put("averageSessionDuration", Math.round(avgDuration * 100.0) / 100.0);

        return statistics;
    }

    @Override
    public boolean canScheduleSession(LocalDateTime startDate, LocalDateTime endDate, UUID teacherId) {
        return canScheduleSession(startDate, endDate, teacherId, null);
    }

    /**
     * Checks if a session can be scheduled (no conflicts with existing sessions)
     *
     * @param startDate the start date
     * @param endDate the end date
     * @param teacherId the teacher ID
     * @param excludeSessionId the session ID to exclude from the check (for updates)
     * @return true if the session can be scheduled, false otherwise
     */
    private boolean canScheduleSession(LocalDateTime startDate, LocalDateTime endDate, UUID teacherId, UUID excludeSessionId) {
        log.debug("Checking if session can be scheduled for teacher ID {} between {} and {}", teacherId, startDate, endDate);

        // Validate teacher existence
        if (!teacherRepository.existsById(teacherId)) {
            throw new ServiceException.ResourceNotFoundException("Teacher", "id", teacherId);
        }

        if (startDate == null || endDate == null) {
            throw new ServiceException.ValidationException("Start date and end date cannot be null");
        }

        if (startDate.isAfter(endDate)) {
            throw new ServiceException.ValidationException("Start date cannot be after end date");
        }

        // Find overlapping sessions
        List<Session> overlappingSessions = repository.findOverlappingSessionsByTeacher(teacherId, startDate, endDate);

        // Exclude the current session if an ID is provided (for updates)
        if (excludeSessionId != null) {
            overlappingSessions = overlappingSessions.stream()
                    .filter(session -> !session.getId().equals(excludeSessionId))
                    .collect(Collectors.toList());
        }

        return overlappingSessions.isEmpty();
    }

    @Override
    public double calculateTeacherTotalHours(UUID teacherId, LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Calculating total hours for teacher ID {} between {} and {}", teacherId, startDate, endDate);

        // Validate teacher existence
        if (!teacherRepository.existsById(teacherId)) {
            throw new ServiceException.ResourceNotFoundException("Teacher", "id", teacherId);
        }

        if (startDate == null || endDate == null) {
            throw new ServiceException.ValidationException("Start date and end date cannot be null");
        }

        if (startDate.isAfter(endDate)) {
            throw new ServiceException.ValidationException("Start date cannot be after end date");
        }

        List<Session> sessions = findByTeacherIdAndDateRange(teacherId, startDate, endDate);

        return sessions.stream()
                .mapToDouble(session ->
                        Duration.between(session.getStartDate(), session.getEndDate()).toMinutes() / 60.0)
                .sum();
    }

    @Override
    public long countByTeacherId(UUID teacherId) {
        log.debug("Counting sessions for teacher ID: {}", teacherId);

        // Validate teacher existence
        if (!teacherRepository.existsById(teacherId)) {
            throw new ServiceException.ResourceNotFoundException("Teacher", "id", teacherId);
        }

        return repository.countByTeacherId(teacherId);
    }

    @Override
    public long countBySubjectId(UUID subjectId) {
        log.debug("Counting sessions for subject ID: {}", subjectId);

        // Validate subject existence
        if (!subjectRepository.existsById(subjectId)) {
            throw new ServiceException.ResourceNotFoundException("Subject", "id", subjectId);
        }

        return repository.countBySubjectId(subjectId);
    }

    @Override
    public void deleteByTeacherId(UUID teacherId) {
        log.debug("Deleting all sessions for teacher ID: {}", teacherId);

        // Validate teacher existence
        if (!teacherRepository.existsById(teacherId)) {
            throw new ServiceException.ResourceNotFoundException("Teacher", "id", teacherId);
        }

        repository.deleteByTeacherId(teacherId);
    }

    @Override
    public void deleteBySubjectId(UUID subjectId) {
        log.debug("Deleting all sessions for subject ID: {}", subjectId);

        // Validate subject existence
        if (!subjectRepository.existsById(subjectId)) {
            throw new ServiceException.ResourceNotFoundException("Subject", "id", subjectId);
        }

        repository.deleteBySubjectId(subjectId);
    }

    /**
     * Validates a session entity
     *
     * @param session the session to validate
     * @throws ServiceException.ValidationException if validation fails
     */
    private void validateSession(Session session) {
        if (session.getStartDate() == null) {
            throw new ServiceException.ValidationException("Session start date cannot be null");
        }

        if (session.getEndDate() == null) {
            throw new ServiceException.ValidationException("Session end date cannot be null");
        }

        if (session.getStartDate().isAfter(session.getEndDate())) {
            throw new ServiceException.ValidationException("Session start date cannot be after end date");
        }

        // Ensure minimum session duration (e.g., 30 minutes)
        long durationMinutes = Duration.between(session.getStartDate(), session.getEndDate()).toMinutes();
        if (durationMinutes < 30) {
            throw new ServiceException.ValidationException("Session duration must be at least 30 minutes");
        }

        if (session.getTeacher() == null || session.getTeacher().getId() == null) {
            throw new ServiceException.ValidationException("Teacher is required for a session");
        }

        if (session.getSubject() == null || session.getSubject().getId() == null) {
            throw new ServiceException.ValidationException("Subject is required for a session");
        }
    }
}