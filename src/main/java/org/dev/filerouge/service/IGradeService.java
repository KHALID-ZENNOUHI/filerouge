package org.dev.filerouge.service;

import org.dev.filerouge.domain.Grade;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing {@link Grade} entities.
 */
public interface IGradeService extends BaseService<Grade> {

    /**
     * Finds all grades for a specific student
     *
     * @param studentId the student ID
     * @return the list of grades
     */
    List<Grade> findByStudentId(UUID studentId);

    /**
     * Finds all grades for a specific student with pagination
     *
     * @param studentId the student ID
     * @param page the page number (0-based)
     * @param size the page size
     * @return a page of grades
     */
    Page<Grade> findByStudentId(UUID studentId, int page, int size);

    /**
     * Finds all grades for a specific activity
     *
     * @param activityId the activity ID
     * @return the list of grades
     */
    List<Grade> findByActivityId(UUID activityId);

    /**
     * Calculates the average grade for a student
     *
     * @param studentId the student ID
     * @return the average grade, or null if the student has no grades
     */
    Float calculateStudentAverage(UUID studentId);

    /**
     * Calculates the average grade for an activity
     *
     * @param activityId the activity ID
     * @return the average grade, or null if the activity has no grades
     */
    Float calculateActivityAverage(UUID activityId);

    /**
     * Deletes all grades for a student
     *
     * @param studentId the student ID
     */
    void deleteByStudentId(UUID studentId);

    /**
     * Deletes all grades for an activity
     *
     * @param activityId the activity ID
     */
    void deleteByActivityId(UUID activityId);
}