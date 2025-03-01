package org.dev.filerouge.service;

import org.dev.filerouge.domain.Absence;
import org.dev.filerouge.domain.Enum.AbsenceStatus;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service interface for managing {@link Absence} entities.
 */
public interface IAbsenceService extends BaseService<Absence> {

    /**
     * Finds all absences for a specific student
     *
     * @param studentId the student ID
     * @return the list of absences
     */
    List<Absence> findByStudentId(UUID studentId);

    /**
     * Finds all absences for a specific student with pagination
     *
     * @param studentId the student ID
     * @param page the page number (0-based)
     * @param size the page size
     * @return a page of absences
     */
    Page<Absence> findByStudentId(UUID studentId, int page, int size);

    /**
     * Finds all absences for a specific class
     *
     * @param classId the class ID
     * @return the list of absences
     */
    List<Absence> findByClassId(UUID classId);

    /**
     * Finds all absences for a specific class with pagination
     *
     * @param classId the class ID
     * @param page the page number (0-based)
     * @param size the page size
     * @return a page of absences
     */
    Page<Absence> findByClassId(UUID classId, int page, int size);

    /**
     * Finds all absences with a specific status
     *
     * @param status the absence status
     * @return the list of absences
     */
    List<Absence> findByStatus(AbsenceStatus status);

    /**
     * Finds all absences with a specific status for a student
     *
     * @param status the absence status
     * @param studentId the student ID
     * @return the list of absences
     */
    List<Absence> findByStatusAndStudentId(AbsenceStatus status, UUID studentId);

    /**
     * Finds all absences between two dates
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return the list of absences
     */
    List<Absence> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Finds all absences between two dates for a specific student
     *
     * @param startDate the start date
     * @param endDate the end date
     * @param studentId the student ID
     * @return the list of absences
     */
    List<Absence> findByDateRangeAndStudentId(LocalDateTime startDate, LocalDateTime endDate, UUID studentId);

    /**
     * Finds all justified absences
     *
     * @return the list of justified absences
     */
    List<Absence> findJustifiedAbsences();

    /**
     * Finds all unjustified absences
     *
     * @return the list of unjustified absences
     */
    List<Absence> findUnjustifiedAbsences();

    /**
     * Justifies an absence
     *
     * @param absenceId the absence ID
     * @param justificationText the justification text
     * @return the updated absence
     */
    Absence justifyAbsence(UUID absenceId, String justificationText);

    /**
     * Marks an absence as unjustified
     *
     * @param absenceId the absence ID
     * @return the updated absence
     */
    Absence markAsUnjustified(UUID absenceId);

    /**
     * Changes the status of an absence
     *
     * @param absenceId the absence ID
     * @param status the new status
     * @return the updated absence
     */
    Absence changeStatus(UUID absenceId, AbsenceStatus status);

    /**
     * Gets absence statistics for a student
     *
     * @param studentId the student ID
     * @return a map of statistics
     */
    Map<String, Object> getStudentAbsenceStatistics(UUID studentId);

    /**
     * Gets absence statistics for a class
     *
     * @param classId the class ID
     * @return a map of statistics
     */
    Map<String, Object> getClassAbsenceStatistics(UUID classId);

    /**
     * Counts the number of absences for a specific student
     *
     * @param studentId the student ID
     * @return the number of absences
     */
    long countByStudentId(UUID studentId);

    /**
     * Counts the number of justified absences for a specific student
     *
     * @param studentId the student ID
     * @return the number of justified absences
     */
    long countJustifiedByStudentId(UUID studentId);

    /**
     * Counts the number of unjustified absences for a specific student
     *
     * @param studentId the student ID
     * @return the number of unjustified absences
     */
    long countUnjustifiedByStudentId(UUID studentId);

    /**
     * Deletes all absences for a specific student
     *
     * @param studentId the student ID
     */
    void deleteByStudentId(UUID studentId);
}