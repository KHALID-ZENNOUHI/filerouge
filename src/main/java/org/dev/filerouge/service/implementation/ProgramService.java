package org.dev.filerouge.service.implementation;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.dev.filerouge.domain.Class;
import org.dev.filerouge.domain.Program;
import org.dev.filerouge.domain.Subject;
import org.dev.filerouge.web.error.ServiceException;
import org.dev.filerouge.repository.ClassRepository;
import org.dev.filerouge.repository.ProgramRepository;
import org.dev.filerouge.repository.SubjectRepository;
import org.dev.filerouge.service.IProgramService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of {@link IProgramService} for managing {@link Program} entities.
 */
@Service
@Transactional
@Slf4j
public class ProgramService extends BaseServiceImpl<Program, ProgramRepository> implements IProgramService {

    private final ClassRepository classRepository;
    private final SubjectRepository subjectRepository;

    public ProgramService(ProgramRepository programRepository,
                          ClassRepository classRepository,
                          SubjectRepository subjectRepository) {
        super(programRepository, "Program");
        this.classRepository = classRepository;
        this.subjectRepository = subjectRepository;
    }

    @Override
    public Program save(Program program) {
        log.debug("Saving program: {}", program);

        // Validate program
        validateProgram(program);

        // Validate class existence
        if (program.getClazz() != null && program.getClazz().getId() != null) {
            UUID classId = program.getClazz().getId();
            if (!classRepository.existsById(classId)) {
                throw new ServiceException.ResourceNotFoundException("Class", "id", classId);
            }
        } else {
            throw new ServiceException.ValidationException("Class is required for a program");
        }

        // Validate subject existence
        if (program.getSubject() != null && program.getSubject().getId() != null) {
            UUID subjectId = program.getSubject().getId();
            if (!subjectRepository.existsById(subjectId)) {
                throw new ServiceException.ResourceNotFoundException("Subject", "id", subjectId);
            }
        } else {
            throw new ServiceException.ValidationException("Subject is required for a program");
        }

        // Check if program already exists for the class and subject
        if (existsByClassAndSubject(program.getClazz().getId(), program.getSubject().getId())) {
            throw new ServiceException.DuplicateResourceException(
                    "Program",
                    "class and subject",
                    program.getClazz().getId() + " and " + program.getSubject().getId()
            );
        }

        return super.save(program);
    }

    @Override
    public Program update(Program program) {
        log.debug("Updating program: {}", program);

        // Check if program exists
        if (!existsById(program.getId())) {
            throw new ServiceException.ResourceNotFoundException("Program", "id", program.getId());
        }

        // Validate program
        validateProgram(program);

        // Validate class existence
        if (program.getClazz() != null && program.getClazz().getId() != null) {
            UUID classId = program.getClazz().getId();
            if (!classRepository.existsById(classId)) {
                throw new ServiceException.ResourceNotFoundException("Class", "id", classId);
            }
        } else {
            throw new ServiceException.ValidationException("Class is required for a program");
        }

        // Validate subject existence
        if (program.getSubject() != null && program.getSubject().getId() != null) {
            UUID subjectId = program.getSubject().getId();
            if (!subjectRepository.existsById(subjectId)) {
                throw new ServiceException.ResourceNotFoundException("Subject", "id", subjectId);
            }
        } else {
            throw new ServiceException.ValidationException("Subject is required for a program");
        }

        // Get existing program to check if class or subject has changed
        Program existingProgram = findById(program.getId());
        UUID oldClassId = existingProgram.getClazz().getId();
        UUID oldSubjectId = existingProgram.getSubject().getId();
        UUID newClassId = program.getClazz().getId();
        UUID newSubjectId = program.getSubject().getId();

        // Check if program already exists for the class and subject (only if class or subject has changed)
        if ((!oldClassId.equals(newClassId) || !oldSubjectId.equals(newSubjectId)) &&
                existsByClassAndSubject(newClassId, newSubjectId)) {
            throw new ServiceException.DuplicateResourceException(
                    "Program",
                    "class and subject",
                    newClassId + " and " + newSubjectId
            );
        }

        return super.update(program);
    }

    @Override
    public List<Program> findByClassId(UUID clazzId) {
        log.debug("Finding programs by class ID: {}", clazzId);

        // Validate class existence
        if (!classRepository.existsById(clazzId)) {
            throw new ServiceException.ResourceNotFoundException("Class", "id", clazzId);
        }

        return repository.findByClazzId(clazzId);
    }

    @Override
    public Page<Program> findByClassId(UUID classId, int page, int size) {
        log.debug("Finding programs by class ID with pagination: {}", classId);

        // Validate class existence
        if (!classRepository.existsById(classId)) {
            throw new ServiceException.ResourceNotFoundException("Class", "id", classId);
        }

        Class aClass = classRepository.findById(classId)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("Class", "id", classId));

        return repository.findByClazz(aClass, PageRequest.of(page, size));
    }

    @Override
    public List<Program> findBySubjectId(UUID subjectId) {
        log.debug("Finding programs by subject ID: {}", subjectId);

        // Validate subject existence
        if (!subjectRepository.existsById(subjectId)) {
            throw new ServiceException.ResourceNotFoundException("Subject", "id", subjectId);
        }

        return repository.findBySubjectId(subjectId);
    }

    @Override
    public Page<Program> findBySubjectId(UUID subjectId, int page, int size) {
        log.debug("Finding programs by subject ID with pagination: {}", subjectId);

        // Validate subject existence
        if (!subjectRepository.existsById(subjectId)) {
            throw new ServiceException.ResourceNotFoundException("Subject", "id", subjectId);
        }

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("Subject", "id", subjectId));

        return repository.findBySubject(subject, PageRequest.of(page, size));
    }

    @Override
    public Program findByClassAndSubject(UUID clazzId, UUID subjectId) {
        log.debug("Finding program by class ID and subject ID: {}, {}", clazzId, subjectId);

        // Validate class existence
        if (!classRepository.existsById(clazzId)) {
            throw new ServiceException.ResourceNotFoundException("Class", "id", clazzId);
        }

        // Validate subject existence
        if (!subjectRepository.existsById(subjectId)) {
            throw new ServiceException.ResourceNotFoundException("Subject", "id", subjectId);
        }

        return repository.findByClazzIdAndSubjectId(clazzId, subjectId)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("Program", "class and subject", clazzId + " and " + subjectId));
    }

    @Override
    public boolean existsByClassAndSubject(UUID clazzId, UUID subjectId) {
        if (clazzId == null || subjectId == null) {
            return false;
        }
        return repository.existsByClazzIdAndSubjectId(clazzId, subjectId);
    }

    @Override
    public Map<String, Object> getClassProgramStatistics(UUID classId) {
        log.debug("Getting program statistics for class ID: {}", classId);

        // Validate class existence
        if (!classRepository.existsById(classId)) {
            throw new ServiceException.ResourceNotFoundException("Class", "id", classId);
        }

        Map<String, Object> statistics = new HashMap<>();

        // Get the class
        Class aClass = classRepository.findById(classId)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("Class", "id", classId));

        // Get all programs for the class
        List<Program> programs = findByClassId(classId);

        statistics.put("className", aClass.getName());
        statistics.put("totalPrograms", programs.size());

        // Get subjects
        List<String> subjectNames = programs.stream()
                .map(program -> program.getSubject().getName())
                .sorted()
                .toList();

        statistics.put("subjects", subjectNames);

        return statistics;
    }

    @Override
    public Map<String, Object> getSubjectProgramStatistics(UUID subjectId) {
        log.debug("Getting program statistics for subject ID: {}", subjectId);

        // Validate subject existence
        if (!subjectRepository.existsById(subjectId)) {
            throw new ServiceException.ResourceNotFoundException("Subject", "id", subjectId);
        }

        Map<String, Object> statistics = new HashMap<>();

        // Get the subject
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("Subject", "id", subjectId));

        // Get all programs for the subject
        List<Program> programs = findBySubjectId(subjectId);

        statistics.put("subjectName", subject.getName());
        statistics.put("totalPrograms", programs.size());

        // Get classes
        List<String> classNames = programs.stream()
                .map(program -> program.getClazz().getName())
                .sorted()
                .toList();

        statistics.put("classes", classNames);

        return statistics;
    }

    @Override
    public long countByClassId(UUID clazzId) {
        log.debug("Counting programs by class ID: {}", clazzId);

        // Validate class existence
        if (!classRepository.existsById(clazzId)) {
            throw new ServiceException.ResourceNotFoundException("Class", "id", clazzId);
        }

        return repository.countByClazzId(clazzId);
    }

    @Override
    public long countBySubjectId(UUID subjectId) {
        log.debug("Counting programs by subject ID: {}", subjectId);

        // Validate subject existence
        if (!subjectRepository.existsById(subjectId)) {
            throw new ServiceException.ResourceNotFoundException("Subject", "id", subjectId);
        }

        return repository.countBySubjectId(subjectId);
    }

    @Override
    public void deleteByClassId(UUID clazzId) {
        log.debug("Deleting all programs for class ID: {}", clazzId);

        // Validate class existence
        if (!classRepository.existsById(clazzId)) {
            throw new ServiceException.ResourceNotFoundException("Class", "id", clazzId);
        }

        repository.deleteByClazzId(clazzId);
    }

    @Override
    public void deleteBySubjectId(UUID subjectId) {
        log.debug("Deleting all programs for subject ID: {}", subjectId);

        // Validate subject existence
        if (!subjectRepository.existsById(subjectId)) {
            throw new ServiceException.ResourceNotFoundException("Subject", "id", subjectId);
        }

        repository.deleteBySubjectId(subjectId);
    }

    @Override
    public Program assignSubjectToClass(UUID classId, UUID subjectId, String description) {
        log.debug("Assigning subject ID {} to class ID {}", subjectId, classId);

        // Validate class existence
        if (!classRepository.existsById(classId)) {
            throw new ServiceException.ResourceNotFoundException("Class", "id", classId);
        }

        // Validate subject existence
        if (!subjectRepository.existsById(subjectId)) {
            throw new ServiceException.ResourceNotFoundException("Subject", "id", subjectId);
        }

        // Check if program already exists
        if (existsByClassAndSubject(classId, subjectId)) {
            throw new ServiceException.DuplicateResourceException(
                    "Program",
                    "class and subject",
                    classId + " and " + subjectId
            );
        }

        // Create and save the program
        Program program = new Program();
        program.setClazz(classRepository.findById(classId).orElseThrow());
        program.setSubject(subjectRepository.findById(subjectId).orElseThrow());
        program.setDescription(description);

        return save(program);
    }

    @Override
    public void removeSubjectFromClass(UUID clazzId, UUID subjectId) {
        log.debug("Removing subject ID {} from class ID {}", subjectId, clazzId);

        // Find the program
        Program program = repository.findByClazzIdAndSubjectId(clazzId, subjectId)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException(
                        "Program",
                        "class and subject",
                        clazzId + " and " + subjectId
                ));

        // Delete the program
        delete(program.getId());
    }

    /**
     * Validates a program entity
     *
     * @param program the program to validate
     * @throws ServiceException.ValidationException if validation fails
     */
    private void validateProgram(Program program) {
        if (program.getClazz() == null || program.getClazz().getId() == null) {
            throw new ServiceException.ValidationException("Class is required for a program");
        }

        if (program.getSubject() == null || program.getSubject().getId() == null) {
            throw new ServiceException.ValidationException("Subject is required for a program");
        }
    }
}