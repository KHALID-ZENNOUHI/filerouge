package org.dev.filerouge.service.implementation;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.dev.filerouge.domain.Class;
import org.dev.filerouge.domain.Level;
import org.dev.filerouge.domain.Program;
import org.dev.filerouge.web.error.ServiceException;
import org.dev.filerouge.repository.ClassRepository;
import org.dev.filerouge.repository.LevelRepository;
import org.dev.filerouge.repository.ProgramRepository;
import org.dev.filerouge.repository.SubjectRepository;
import org.dev.filerouge.service.IClassService;
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
    private final ProgramRepository programRepository;
    private final SubjectRepository subjectRepository;

    public ClassService(ClassRepository classRepository,
                        LevelRepository levelRepository,
                        ProgramRepository programRepository,
                        SubjectRepository subjectRepository) {
        super(classRepository, "Class");
        this.levelRepository = levelRepository;
        this.programRepository = programRepository;
        this.subjectRepository = subjectRepository;
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

        // Validate program existence if provided
        if (aClass.getProgram() != null && aClass.getProgram().getId() != null) {
            UUID programId = aClass.getProgram().getId();
            if (!programRepository.existsById(programId)) {
                throw new ServiceException.ResourceNotFoundException("Program", "id", programId);
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

        // Validate program existence if provided
        if (aClass.getProgram() != null && aClass.getProgram().getId() != null) {
            UUID programId = aClass.getProgram().getId();
            if (!programRepository.existsById(programId)) {
                throw new ServiceException.ResourceNotFoundException("Program", "id", programId);
            }
        }

        // Preserve program relationship if not explicitly set
        if (aClass.getProgram() == null && existingClass.getProgram() != null) {
            aClass.setProgram(existingClass.getProgram());
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

    @Override
    public List<Class> findByProgramId(UUID programId) {
        log.debug("Finding classes by program ID: {}", programId);

        // Validate program existence
        if (!programRepository.existsById(programId)) {
            throw new ServiceException.ResourceNotFoundException("Program", "id", programId);
        }

        return repository.findByProgramId(programId);
    }

    @Override
    public Page<Class> findByProgramId(UUID programId, int page, int size) {
        log.debug("Finding classes by program ID with pagination: {}", programId);

        // Validate program existence
        if (!programRepository.existsById(programId)) {
            throw new ServiceException.ResourceNotFoundException("Program", "id", programId);
        }

        return repository.findByProgramId(programId, PageRequest.of(page, size));
    }

    @Override
    public List<Class> findBySubjectId(UUID subjectId) {
        log.debug("Finding classes by subject ID: {}", subjectId);

        // Validate subject existence
        if (!subjectRepository.existsById(subjectId)) {
            throw new ServiceException.ResourceNotFoundException("Subject", "id", subjectId);
        }

        return repository.findByProgramSubjectsId(subjectId);
    }

    @Override
    public long countByProgramId(UUID programId) {
        log.debug("Counting classes by program ID: {}", programId);

        // Validate program existence
        if (!programRepository.existsById(programId)) {
            throw new ServiceException.ResourceNotFoundException("Program", "id", programId);
        }

        return repository.countByProgramId(programId);
    }

    @Override
    public Class assignToProgram(UUID classId, UUID programId) {
        log.debug("Assigning class ID {} to program ID {}", classId, programId);

        // Validate class existence
        Class aClass = findById(classId);

        // Validate program existence
        if (!programRepository.existsById(programId)) {
            throw new ServiceException.ResourceNotFoundException("Program", "id", programId);
        }
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("Program", "id", programId));

        // Check if class is already assigned to this program
        if (aClass.getProgram() != null && aClass.getProgram().getId().equals(programId)) {
            return aClass; // Already assigned
        }

        // If class is already assigned to another program, remove from that program first
        if (aClass.getProgram() != null) {
            // Remove the class from its current program's classes list
            Program currentProgram = aClass.getProgram();
            currentProgram.getClasses().remove(aClass);
            programRepository.save(currentProgram);
        }

        // Assign class to new program
        aClass.setProgram(program);
        program.getClasses().add(aClass);

        // Save both entities
        programRepository.save(program);
        return repository.save(aClass);
    }

    @Override
    public Class removeFromProgram(UUID classId) {
        log.debug("Removing class ID {} from its program", classId);

        // Validate class existence
        Class aClass = findById(classId);

        // If class is not assigned to a program, nothing to do
        if (aClass.getProgram() == null) {
            return aClass;
        }

        // Remove the class from its program's classes list
        Program program = aClass.getProgram();
        program.getClasses().remove(aClass);

        // Clear the program reference from the class
        aClass.setProgram(null);

        // Save both entities
        programRepository.save(program);
        return repository.save(aClass);
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