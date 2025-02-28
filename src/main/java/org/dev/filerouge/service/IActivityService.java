package org.dev.filerouge.service;

import org.dev.filerouge.domain.Activity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public interface IActivityService {
    Activity save(Activity activity);

    Activity update(Activity activity);

    Activity findById(UUID id);

    void delete(UUID id);

    Page<Activity> findAll(int page, int size);
}
