package org.dev.filerouge.service;

import org.dev.filerouge.domain.Activity;
import org.dev.filerouge.domain.Enum.ActivityType;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing {@link Activity} entities.
 */
public interface IActivityService extends BaseService<Activity> {

    /**
     * Finds all activities for a specific subject
     *
     * @param subjectId the subject ID
     * @return the list of activities
     */
    List<Activity> findBySubjectId(UUID subjectId);

    /**
     * Finds all activities for a specific subject with pagination
     *
     * @param subjectId the subject ID
     * @param page the page number (0-based)
     * @param size the page size
     * @return a page of activities
     */
    Page<Activity> findBySubjectId(UUID subjectId, int page, int size);

    /**
     * Finds all activities of a specific type
     *
     * @param type the activity type
     * @return the list of activities
     */
    List<Activity> findByType(ActivityType type);

    /**
     * Finds all activities scheduled between two dates
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return the list of activities
     */
    List<Activity> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Searches for activities by title (partial match, case insensitive)
     *
     * @param title the title to search for
     * @return the list of matching activities
     */
    List<Activity> searchByTitle(String title);
}