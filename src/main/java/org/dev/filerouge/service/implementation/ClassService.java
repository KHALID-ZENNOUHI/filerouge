package org.dev.filerouge.service.implementation;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.dev.filerouge.domain.Class;
import org.dev.filerouge.domain.Level;
import org.dev.filerouge.web.error.ServiceException;
import org.dev.filerouge.repository.ClassRepository;
import org.dev.filerouge.repository.LevelRepository;
import org.dev.filerouge.service.IClassService;
import org.dev.filerouge.service.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of {@link IClassService} for managing {@link Class} entities.
 */
@Service
@Transactional
@Slf4j
public class ClassService extends BaseServiceImpl<Class, ClassRepository> implements IClassService {

    private final LevelRepository levelRepository;

    public ClassService(ClassRepository classRepository, LevelRepository levelRepository) {
        super(classRepository, "Class");
        this.levelRepository = levelRepository;
    }

    @Override
    public Class save(Class aClass) {
        log.debug("Saving class: {}", aClass);

        // Validate class
        validateClass(aClass);

        // Validate name uniqueness
        if (existsByName(aClass.getName())) {
            throw new ServiceException.DuplicateResourceException("Class", "name", aClass.getName());
        }

        // Validate level existence
        if (aClass.getLevel() != null && aClass.getLevel().getId() != null) {
            UUID levelId = aClass.getLevel().getId();
            if (!levelRepository.existsById(levelId)) {
                throw new ServiceException.ResourceNotFoundException("Level", "id", levelId);
            }
        }

        return super.save(aClass);
    }

    @Override
    public Class update(Class aClass) {
        log.debug("Updating class: {}", aClass);

        // Check if class exists
        if (!existsById(aClass.getId())) {
            throw new ServiceException.ResourceNotFoundException("Class", "id", aClass.getId());
        }

        // Validate class
        validateClass(aClass);

        // Validate name uniqueness (only if name is changed)
        Class existingClass = findById(aClass.getId());
        if (!existingClass.getName().equals(aClass.getName()) && existsByName(aClass.getName())) {
            throw new ServiceException.DuplicateResourceException("Class", "name", aClass.getName());
        }

        // Validate level existence
        if (aClass.getLevel() != null && aClass.getLevel().getId() != null) {
            UUID levelId = aClass.getLevel().getId();
            if (!levelRepository.existsById(levelId)) {
                throw new ServiceException.ResourceNotFoundException("Level", "id", levelId);
            }
        }

        return super.update(aClass);
    }

    @Override
    public Class findByName(String name) {
        log.debug("Finding class by name: {}", name);

        if (name == null || name.trim().isEmpty()) {
            throw new ServiceException.ValidationException("Class name cannot be empty");
        }

        return repository.findByName(name)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("Class", "name", name));
    }

    @Override
    public boolean existsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return repository.existsByName(name);
    }

    @Override
    public List<Class> findByLevelId(UUID levelId) {
        log.debug("Finding classes by level ID: {}", levelId);

        // Validate level existence
        if (!levelRepository.existsById(levelId)) {
            throw new ServiceException.ResourceNotFoundException("Level", "id", levelId);
        }

        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("Level", "id", levelId));

        return repository.findByLevel(level);
    }

    @Override
    public Page<Class> findByLevelId(UUID levelId, int page, int size) {
        log.debug("Finding classes by level ID with pagination: {}", levelId);

        // Validate level existence
        if (!levelRepository.existsById(levelId)) {
            throw new ServiceException.ResourceNotFoundException("Level", "id", levelId);
        }

        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("Level", "id", levelId));

        return repository.findByLevel(level, PageRequest.of(page, size));
    }

    @Override
    public List<Class> findByDepartmentId(UUID departmentId) {
        log.debug("Finding classes by department ID: {}", departmentId);

        return repository.findByLevelDepartmentId(departmentId);
    }

    @Override
    public long countByLevelId(UUID levelId) {
        log.debug("Counting classes by level ID: {}", levelId);

        // Validate level existence
        if (!levelRepository.existsById(levelId)) {
            throw new ServiceException.ResourceNotFoundException("Level", "id", levelId);
        }

        return repository.countByLevelId(levelId);
    }

    /**
     * Validates a class entity
     *
     * @param aClass the class to validate
     * @throws ServiceException.ValidationException if validation fails
     */
    private void validateClass(Class aClass) {
        if (aClass.getName() == null || aClass.getName().trim().isEmpty()) {
            throw new ServiceException.ValidationException("Class name cannot be empty");
        }
    }
}