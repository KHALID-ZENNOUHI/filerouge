package org.dev.filerouge.service.implementation;

import lombok.RequiredArgsConstructor;
import org.dev.filerouge.domain.Level;
import org.dev.filerouge.repository.LevelRepository;
import org.dev.filerouge.service.ILevelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LevelService implements ILevelService {
    private final LevelRepository levelRepository;

    @Override
    public Level save(Level level) {
        return levelRepository.save(level);
    }

    @Override
    public Level update(Level level) {
        Optional<Level> existingLevel = levelRepository.findById(level.getId());
        if (existingLevel.isEmpty()) {
            throw new IllegalArgumentException("Level not found with ID: " + level.getId());
        }
        return levelRepository.save(level);
    }

    @Override
    public Level findById(UUID id) {
        return levelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Level not found with ID: " + id));
    }

    @Override
    public void delete(UUID id) {
        if (!levelRepository.existsById(id)) {
            throw new IllegalArgumentException("Level not found with ID: " + id);
        }
        levelRepository.deleteById(id);
    }

    @Override
    public Page<Level> findAll(int page, int size) {
        return levelRepository.findAll(PageRequest.of(page, size));
    }
}

