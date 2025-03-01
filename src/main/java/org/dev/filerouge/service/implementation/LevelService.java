package org.dev.filerouge.service.implementation;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.dev.filerouge.domain.Department;
import org.dev.filerouge.domain.Level;
import org.dev.filerouge.web.error.ServiceException;
import org.dev.filerouge.repository.DepartmentRepository;
import org.dev.filerouge.repository.LevelRepository;
import org.dev.filerouge.service.ILevelService;
import org.dev.filerouge.service.implementation.BaseServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of {@link ILevelService} for managing {@link Level} entities.
 */
@Service
@Transactional
@Slf4j
public class LevelService extends BaseServiceImpl<Level, LevelRepository> implements ILevelService {

    private final DepartmentRepository departmentRepository;

    public LevelService(LevelRepository levelRepository, DepartmentRepository departmentRepository) {
        super(levelRepository, "Level");
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Level save(Level level) {
        log.debug("Saving level: {}", level);

        // Validate level
        validateLevel(level);

        // Check if department exists
        if (level.getDepartment() != null && level.getDepartment().getId() != null) {
            UUID departmentId = level.getDepartment().getId();
            if (!departmentRepository.existsById(departmentId)) {
                throw new ServiceException.ResourceNotFoundException("Department", "id", departmentId);
            }

            // Check if level name is unique within department
            if (existsByNameAndDepartmentId(level.getName(), departmentId)) {
                throw new ServiceException.DuplicateResourceException("Level", "name", level.getName() + " in department " + departmentId);
            }
        } else {
            // Check if level name is globally unique if no department is specified
            if (existsByName(level.getName())) {
                throw new ServiceException.DuplicateResourceException("Level", "name", level.getName());
            }
        }

        return super.save(level);
    }

    @Override
    public Level update(Level level) {
        log.debug("Updating level: {}", level);

        // Check if level exists
        if (!existsById(level.getId())) {
            throw new ServiceException.ResourceNotFoundException("Level", "id", level.getId());
        }

        // Validate level
        validateLevel(level);

        // Get existing level to check if name has changed
        Level existingLevel = findById(level.getId());
        String oldName = existingLevel.getName();
        UUID oldDepartmentId = existingLevel.getDepartment() != null ? existingLevel.getDepartment().getId() : null;

        // Check if department exists
        if (level.getDepartment() != null && level.getDepartment().getId() != null) {
            UUID departmentId = level.getDepartment().getId();
            if (!departmentRepository.existsById(departmentId)) {
                throw new ServiceException.ResourceNotFoundException("Department", "id", departmentId);
            }

            // Check if level name is unique within department (only if name or department has changed)
            if (!level.getName().equals(oldName) ||
                    !departmentId.equals(oldDepartmentId)) {

                if (existsByNameAndDepartmentId(level.getName(), departmentId)) {
                    throw new ServiceException.DuplicateResourceException("Level", "name", level.getName() + " in department " + departmentId);
                }
            }
        } else if (!level.getName().equals(oldName)) {
            // Check if level name is globally unique if no department is specified (only if name has changed)
            if (existsByName(level.getName())) {
                throw new ServiceException.DuplicateResourceException("Level", "name", level.getName());
            }
        }

        return super.update(level);
    }

    @Override
    public Level findByName(String name) {
        log.debug("Finding level by name: {}", name);

        if (name == null || name.trim().isEmpty()) {
            throw new ServiceException.ValidationException("Level name cannot be empty");
        }

        return repository.findByName(name)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("Level", "name", name));
    }

    @Override
    public boolean existsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return repository.existsByName(name);
    }

    @Override
    public List<Level> findByDepartmentId(UUID departmentId) {
        log.debug("Finding levels by department ID: {}", departmentId);

        // Validate department existence
        if (!departmentRepository.existsById(departmentId)) {
            throw new ServiceException.ResourceNotFoundException("Department", "id", departmentId);
        }

        return repository.findByDepartmentId(departmentId);
    }

    @Override
    public Page<Level> findByDepartmentId(UUID departmentId, int page, int size) {
        log.debug("Finding levels by department ID with pagination: {}", departmentId);

        // Validate department existence
        if (!departmentRepository.existsById(departmentId)) {
            throw new ServiceException.ResourceNotFoundException("Department", "id", departmentId);
        }

        return repository.findByDepartmentId(departmentId, PageRequest.of(page, size));
    }

    @Override
    public boolean existsByNameAndDepartmentId(String name, UUID departmentId) {
        if (name == null || name.trim().isEmpty() || departmentId == null) {
            return false;
        }
        return repository.existsByNameAndDepartmentId(name, departmentId);
    }

    @Override
    public long countByDepartmentId(UUID departmentId) {
        log.debug("Counting levels by department ID: {}", departmentId);

        // Validate department existence
        if (!departmentRepository.existsById(departmentId)) {
            throw new ServiceException.ResourceNotFoundException("Department", "id", departmentId);
        }

        return repository.countByDepartmentId(departmentId);
    }

    @Override
    public List<Level> searchByName(String searchTerm) {
        log.debug("Searching levels by name: {}", searchTerm);

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new ServiceException.ValidationException("Search term cannot be empty");
        }

        return repository.findByNameContainingIgnoreCase(searchTerm.trim());
    }

    @Override
    public long countClassesByLevelId(UUID levelId) {
        log.debug("Counting classes by level ID: {}", levelId);

        // Validate level existence
        if (!existsById(levelId)) {
            throw new ServiceException.ResourceNotFoundException("Level", "id", levelId);
        }

        return repository.countClassesByLevelId(levelId);
    }

    @Override
    public String getLevelHierarchyPath(UUID levelId) {
        log.debug("Getting hierarchy path for level ID: {}", levelId);

        Level level = findById(levelId);

        StringBuilder path = new StringBuilder();

        if (level.getDepartment() != null) {
            path.append(level.getDepartment().getName())
                    .append(" > ");
        }

        path.append(level.getName());

        return path.toString();
    }

    /**
     * Validates a level entity
     *
     * @param level the level to validate
     * @throws ServiceException.ValidationException if validation fails
     */
    private void validateLevel(Level level) {
        if (level.getName() == null || level.getName().trim().isEmpty()) {
            throw new ServiceException.ValidationException("Level name cannot be empty");
        }
    }
}