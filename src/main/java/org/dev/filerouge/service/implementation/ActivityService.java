package org.dev.filerouge.service.implementation;

import lombok.RequiredArgsConstructor;
import org.dev.filerouge.domain.Activity;
import org.dev.filerouge.repository.ActivityRepository;
import org.dev.filerouge.service.IActivityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivityService implements IActivityService {
    private final ActivityRepository activityRepository;

    @Override
    public Activity save(Activity activity) {
        return activityRepository.save(activity);
    }

    @Override
    public Activity update(Activity activity) {
        Optional<Activity> existingActivity = activityRepository.findById(activity.getId());
        if (existingActivity.isEmpty()) {
            throw new IllegalArgumentException("Activity not found with ID: " + activity.getId());
        }
        return activityRepository.save(activity);
    }

    @Override
    public Activity findById(UUID id) {
        return activityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found with ID: " + id));
    }

    @Override
    public void delete(UUID id) {
        if (!activityRepository.existsById(id)) {
            throw new IllegalArgumentException("Activity not found with ID: " + id);
        }
        activityRepository.deleteById(id);
    }

    @Override
    public Page<Activity> findAll(int page, int size) {
        return activityRepository.findAll(PageRequest.of(page, size));
    }
}

