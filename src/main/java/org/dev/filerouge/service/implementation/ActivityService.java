package org.dev.filerouge.service.implementation;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.dev.filerouge.domain.Activity;
import org.dev.filerouge.domain.Enum.ActivityType;
import org.dev.filerouge.domain.Subject;
import org.dev.filerouge.web.error.ServiceException;
import org.dev.filerouge.repository.ActivityRepository;
import org.dev.filerouge.repository.SubjectRepository;
import org.dev.filerouge.service.IActivityService;
import org.dev.filerouge.service.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of {@link IActivityService} for managing {@link Activity} entities.
 */
@Service
@Transactional
@Slf4j
public class ActivityService extends BaseServiceImpl<Activity, ActivityRepository> implements IActivityService {

    private final SubjectRepository subjectRepository;

    public ActivityService(ActivityRepository activityRepository, SubjectRepository subjectRepository) {
        super(activityRepository, "Activity");
        this.subjectRepository = subjectRepository;
    }

    @Override
    public Activity save(Activity activity) {
        log.debug("Saving activity: {}", activity);

        // Validate activity
        validateActivity(activity);

        // Validate subject existence
        if (activity.getSubject() != null && activity.getSubject().getId() != null) {
            UUID subjectId = activity.getSubject().getId();
            if (!subjectRepository.existsById(subjectId)) {
                throw new ServiceException.ResourceNotFoundException("Subject", "id", subjectId);
            }
        }

        return super.save(activity);
    }

    @Override
    public Activity update(Activity activity) {
        log.debug("Updating activity: {}", activity);

        // Check if activity exists
        if (!existsById(activity.getId())) {
            throw new ServiceException.ResourceNotFoundException("Activity", "id", activity.getId());
        }

        // Validate activity
        validateActivity(activity);

        // Validate subject existence
        if (activity.getSubject() != null && activity.getSubject().getId() != null) {
            UUID subjectId = activity.getSubject().getId();
            if (!subjectRepository.existsById(subjectId)) {
                throw new ServiceException.ResourceNotFoundException("Subject", "id", subjectId);
            }
        }

        return super.update(activity);
    }

    @Override
    public List<Activity> findBySubjectId(UUID subjectId) {
        log.debug("Finding activities by subject ID: {}", subjectId);

        // Validate subject existence
        if (!subjectRepository.existsById(subjectId)) {
            throw new ServiceException.ResourceNotFoundException("Subject", "id", subjectId);
        }

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("Subject", "id", subjectId));

        return repository.findBySubject(subject);
    }

    @Override
    public Page<Activity> findBySubjectId(UUID subjectId, int page, int size) {
        log.debug("Finding activities by subject ID with pagination: {}", subjectId);

        // Validate subject existence
        if (!subjectRepository.existsById(subjectId)) {
            throw new ServiceException.ResourceNotFoundException("Subject", "id", subjectId);
        }

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("Subject", "id", subjectId));

        return repository.findBySubject(subject, PageRequest.of(page, size));
    }

    @Override
    public List<Activity> findByType(ActivityType type) {
        log.debug("Finding activities by type: {}", type);

        if (type == null) {
            throw new ServiceException.ValidationException("Activity type cannot be null");
        }

        return repository.findByType(type);
    }

    @Override
    public List<Activity> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Finding activities between dates: {} and {}", startDate, endDate);

        if (startDate == null || endDate == null) {
            throw new ServiceException.ValidationException("Start date and end date cannot be null");
        }

        if (startDate.isAfter(endDate)) {
            throw new ServiceException.ValidationException("Start date cannot be after end date");
        }

        return repository.findByDateBetween(startDate, endDate);
    }

    @Override
    public List<Activity> searchByTitle(String title) {
        log.debug("Searching activities by title: {}", title);

        if (title == null || title.trim().isEmpty()) {
            throw new ServiceException.ValidationException("Search title cannot be empty");
        }

        return repository.findByTitleContainingIgnoreCase(title.trim());
    }

    /**
     * Validates an activity entity
     *
     * @param activity the activity to validate
     * @throws ServiceException.ValidationException if validation fails
     */
    private void validateActivity(Activity activity) {
        if (activity.getType() == null) {
            throw new ServiceException.ValidationException("Activity type is required");
        }

        if (activity.getTitle() == null || activity.getTitle().trim().isEmpty()) {
            throw new ServiceException.ValidationException("Activity title cannot be empty");
        }
    }
}