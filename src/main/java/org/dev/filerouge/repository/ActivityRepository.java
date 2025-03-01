package org.dev.filerouge.repository;

import org.dev.filerouge.domain.Activity;
import org.dev.filerouge.domain.Enum.ActivityType;
import org.dev.filerouge.domain.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, UUID> {

    /**
     * Finds all activities for a specific subject
     *
     * @param subject the subject
     * @return the list of activities
     */
    List<Activity> findBySubject(Subject subject);

    /**
     * Finds all activities for a specific subject with pagination
     *
     * @param subject the subject
     * @param pageable the pagination information
     * @return a page of activities
     */
    Page<Activity> findBySubject(Subject subject, Pageable pageable);

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
    List<Activity> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Finds all activities for a specific title (partial match, case insensitive)
     *
     * @param title the title to search for
     * @return the list of activities
     */
    List<Activity> findByTitleContainingIgnoreCase(String title);
}