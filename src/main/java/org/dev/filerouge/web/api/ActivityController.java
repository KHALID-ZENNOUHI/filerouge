package org.dev.filerouge.web.api;

import lombok.RequiredArgsConstructor;
import org.dev.filerouge.domain.Activity;
import org.dev.filerouge.service.IActivityService;
import org.dev.filerouge.service.implementation.ActivityService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {
    private final IActivityService activityService;

    @PostMapping
    public ResponseEntity<Activity> createActivity(@RequestBody Activity activity) {
        return ResponseEntity.ok(activityService.save(activity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Activity> updateActivity(@PathVariable UUID id, @RequestBody Activity activity) {
        activity.setId(id);
        return ResponseEntity.ok(activityService.update(activity));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Activity> getActivityById(@PathVariable UUID id) {
        return ResponseEntity.ok(activityService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable UUID id) {
        activityService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<Activity>> getAllActivities(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(activityService.findAll(page, size));
    }
}
