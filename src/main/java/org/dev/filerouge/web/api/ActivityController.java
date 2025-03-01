package org.dev.filerouge.web.api;

import org.dev.filerouge.domain.Activity;
import org.dev.filerouge.domain.Enum.ActivityType;
import org.dev.filerouge.service.IActivityService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing {@link Activity} entities.
 */
@RestController
@RequestMapping("/api/activities")
public class ActivityController extends BaseController<Activity, IActivityService> {

    public ActivityController(IActivityService activityService) {
        super(activityService);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setId(Activity activity, UUID id) {
        activity.setId(id);
    }

    /**
     * Finds all activities for a specific subject.
     *
     * @param subjectId The subject ID
     * @return The list of activities
     */
    @GetMapping("/by-subject/{subjectId}")
    public ResponseEntity<List<Activity>> findBySubjectId(@PathVariable UUID subjectId) {
        List<Activity> activities = service.findBySubjectId(subjectId);
        return ResponseEntity.ok(activities);
    }

    /**
     * Finds all activities for a specific subject with pagination.
     *
     * @param subjectId The subject ID
     * @param page The page number (0-indexed)
     * @param size The page size
     * @return A page of activities
     */
    @GetMapping("/by-subject/{subjectId}/paged")
    public ResponseEntity<Page<Activity>> findBySubjectIdPaged(
            @PathVariable UUID subjectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Activity> activities = service.findBySubjectId(subjectId, page, size);
        return ResponseEntity.ok(activities);
    }

    /**
     * Finds all activities of a specific type.
     *
     * @param type The activity type
     * @return The list of activities
     */
    @GetMapping("/by-type/{type}")
    public ResponseEntity<List<Activity>> findByType(@PathVariable ActivityType type) {
        List<Activity> activities = service.findByType(type);
        return ResponseEntity.ok(activities);
    }

    /**
     * Finds all activities scheduled between two dates.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return The list of activities
     */
    @GetMapping("/by-date-range")
    public ResponseEntity<List<Activity>> findByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Activity> activities = service.findByDateRange(startDate, endDate);
        return ResponseEntity.ok(activities);
    }

    /**
     * Searches for activities by title.
     *
     * @param title The title to search for
     * @return The list of matching activities
     */
    @GetMapping("/search")
    public ResponseEntity<List<Activity>> searchByTitle(@RequestParam String title) {
        List<Activity> activities = service.searchByTitle(title);
        return ResponseEntity.ok(activities);
    }
}