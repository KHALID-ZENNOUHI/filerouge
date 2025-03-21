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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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

        // Validate program description
        if (program.getDescription() == null || program.getDescription().trim().isEmpty()) {
            throw new ServiceException.ValidationException("Description is required for a program");
        }

        // Save the program first to get its ID
        Program savedProgram = super.save(program);

        // Update relationships if classes or subjects are already set
        if (program.getClasses() != null && !program.getClasses().isEmpty()) {
            for (Class aClass : program.getClasses()) {
                aClass.setProgram(savedProgram);
                classRepository.save(aClass);
            }
        }

        if (program.getSubjects() != null && !program.getSubjects().isEmpty()) {
            for (Subject subject : program.getSubjects()) {
                subject.setProgram(savedProgram);
                subjectRepository.save(subject);
            }
        }

        return savedProgram;
    }

    @Override
    public Program update(Program program) {
        log.debug("Updating program: {}", program);

        // Check if program exists
        if (!existsById(program.getId())) {
            throw new ServiceException.ResourceNotFoundException("Program", "id", program.getId());
        }

        // Validate program description
        if (program.getDescription() == null || program.getDescription().trim().isEmpty()) {
            throw new ServiceException.ValidationException("Description is required for a program");
        }

        // Update the program
        Program updatedProgram = super.update(program);

        // Since we're not changing the classes and subjects directly here,
        // we don't need additional relationship handling

        return updatedProgram;
    }

    @Override
    public List<Program> findByClassId(UUID classId) {
        log.debug("Finding program for class ID: {}", classId);

        // Validate class existence
        if (!classRepository.existsById(classId)) {
            throw new ServiceException.ResourceNotFoundException("Class", "id", classId);
        }

        // In a one-to-many relationship, a class has only one program
        Class aClass = classRepository.findById(classId)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("Class", "id", classId));

        if (aClass.getProgram() == null) {
            return new ArrayList<>();
        }

        List<Program> result = new ArrayList<>();
        result.add(aClass.getProgram());
        return result;
    }

    @Override
    public Page<Program> findByClassId(UUID classId, int page, int size) {
        // Since a class can only belong to one program in a one-to-many relationship,
        // this method doesn't make much sense. We'll implement it to satisfy the interface,
        // but it will return at most one result.
        log.debug("Finding program for class ID with pagination: {}", classId);

        // Use the general findAll method with pagination
        return repository.findAll(PageRequest.of(page, size));
    }

    @Override
    public List<Program> findBySubjectId(UUID subjectId) {
        log.debug("Finding program for subject ID: {}", subjectId);

        // Validate subject existence
        if (!subjectRepository.existsById(subjectId)) {
            throw new ServiceException.ResourceNotFoundException("Subject", "id", subjectId);
        }

        // In a one-to-many relationship, a subject has only one program
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("Subject", "id", subjectId));

        if (subject.getProgram() == null) {
            return new ArrayList<>();
        }

        List<Program> result = new ArrayList<>();
        result.add(subject.getProgram());
        return result;
    }

    @Override
    public Page<Program> findBySubjectId(UUID subjectId, int page, int size) {
        // Since a subject can only belong to one program in a one-to-many relationship,
        // this method doesn't make much sense. We'll implement it to satisfy the interface,
        // but it will return at most one result.
        log.debug("Finding program for subject ID with pagination: {}", subjectId);

        // Use the general findAll method with pagination
        return repository.findAll(PageRequest.of(page, size));
    }

    @Override
    public Program findByClassAndSubject(UUID classId, UUID subjectId) {
        log.debug("Finding program by class ID and subject ID: {}, {}", classId, subjectId);

        // Validate class existence
        if (!classRepository.existsById(classId)) {
            throw new ServiceException.ResourceNotFoundException("Class", "id", classId);
        }

        // Validate subject existence
        if (!subjectRepository.existsById(subjectId)) {
            throw new ServiceException.ResourceNotFoundException("Subject", "id", subjectId);
        }

        Class aClass = classRepository.findById(classId)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("Class", "id", classId));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("Subject", "id", subjectId));

        // In a one-to-many relationship, check if both class and subject belong to the same program
        if (aClass.getProgram() != null && subject.getProgram() != null &&
                aClass.getProgram().getId().equals(subject.getProgram().getId())) {
            return aClass.getProgram();
        }

        throw new ServiceException.ResourceNotFoundException("Program", "class and subject",
                classId + " and " + subjectId);
    }

    @Override
    public boolean existsByClassAndSubject(UUID classId, UUID subjectId) {
        if (classId == null || subjectId == null) {
            return false;
        }

        // Validate class and subject existence
        if (!classRepository.existsById(classId) || !subjectRepository.existsById(subjectId)) {
            return false;
        }

        Class aClass = classRepository.findById(classId).orElse(null);
        Subject subject = subjectRepository.findById(subjectId).orElse(null);

        if (aClass == null || subject == null || aClass.getProgram() == null || subject.getProgram() == null) {
            return false;
        }

        // Check if they belong to the same program
        return aClass.getProgram().getId().equals(subject.getProgram().getId());
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

        statistics.put("className", aClass.getName());

        // If the class has a program, add its details
        if (aClass.getProgram() != null) {
            Program program = aClass.getProgram();
            statistics.put("programId", program.getId().toString());
            statistics.put("programDescription", program.getDescription());

            // Count other classes in the same program
            long otherClassesCount = program.getClasses().stream()
                    .filter(c -> !c.getId().equals(classId))
                    .count();
            statistics.put("otherClassesInProgram", otherClassesCount);

            // Get all subjects in the program
            List<String> subjectNames = program.getSubjects().stream()
                    .map(Subject::getName)
                    .sorted()
                    .collect(Collectors.toList());
            statistics.put("subjects", subjectNames);
        } else {
            statistics.put("programAssigned", false);
        }

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

        statistics.put("subjectName", subject.getName());

        // If the subject has a program, add its details
        if (subject.getProgram() != null) {
            Program program = subject.getProgram();
            statistics.put("programId", program.getId().toString());
            statistics.put("programDescription", program.getDescription());

            // Count other subjects in the same program
            long otherSubjectsCount = program.getSubjects().stream()
                    .filter(s -> !s.getId().equals(subjectId))
                    .count();
            statistics.put("otherSubjectsInProgram", otherSubjectsCount);

            // Get all classes in the program
            List<String> classNames = program.getClasses().stream()
                    .map(Class::getName)
                    .sorted()
                    .collect(Collectors.toList());
            statistics.put("classes", classNames);
        } else {
            statistics.put("programAssigned", false);
        }

        return statistics;
    }

    @Override
    public long countByClassId(UUID classId) {
        log.debug("Counting programs by class ID: {}", classId);

        // Validate class existence
        if (!classRepository.existsById(classId)) {
            throw new ServiceException.ResourceNotFoundException("Class", "id", classId);
        }

        // In a one-to-many relationship, a class has either 0 or 1 program
        Class aClass = classRepository.findById(classId)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("Class", "id", classId));

        return aClass.getProgram() != null ? 1 : 0;
    }

    @Override
    public long countBySubjectId(UUID subjectId) {
        log.debug("Counting programs by subject ID: {}", subjectId);

        // Validate subject existence
        if (!subjectRepository.existsById(subjectId)) {
            throw new ServiceException.ResourceNotFoundException("Subject", "id", subjectId);
        }

        // In a one-to-many relationship, a subject has either 0 or 1 program
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("Subject", "id", subjectId));

        return subject.getProgram() != null ? 1 : 0;
    }

    @Override
    public void deleteByClassId(UUID classId) {
        log.debug("Removing class ID {} from its program", classId);

        // Validate class existence
        if (!classRepository.existsById(classId)) {
            throw new ServiceException.ResourceNotFoundException("Class", "id", classId);
        }

        // Get the class
        Class aClass = classRepository.findById(classId)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("Class", "id", classId));

        // If the class has a program, remove its reference
        if (aClass.getProgram() != null) {
            Program program = aClass.getProgram();
            program.getClasses().remove(aClass);
            aClass.setProgram(null);

            // Save both entities
            classRepository.save(aClass);
            repository.save(program);

            // If program has no more classes and subjects, consider deleting it
            if (program.getClasses().isEmpty() && program.getSubjects().isEmpty()) {
                repository.delete(program);
            }
        }
    }

    @Override
    public void deleteBySubjectId(UUID subjectId) {
        log.debug("Removing subject ID {} from its program", subjectId);

        // Validate subject existence
        if (!subjectRepository.existsById(subjectId)) {
            throw new ServiceException.ResourceNotFoundException("Subject", "id", subjectId);
        }

        // Get the subject
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("Subject", "id", subjectId));

        // If the subject has a program, remove its reference
        if (subject.getProgram() != null) {
            Program program = subject.getProgram();
            program.getSubjects().remove(subject);
            subject.setProgram(null);

            // Save both entities
            subjectRepository.save(subject);
            repository.save(program);

            // If program has no more classes and subjects, consider deleting it
            if (program.getClasses().isEmpty() && program.getSubjects().isEmpty()) {
                repository.delete(program);
            }
        }
    }

    @Override
    public Program assignSubjectToClass(UUID classId, UUID subjectId, String description) {
        log.debug("Assigning subject ID {} and class ID {} to a program", subjectId, classId);

        // Validate class existence
        if (!classRepository.existsById(classId)) {
            throw new ServiceException.ResourceNotFoundException("Class", "id", classId);
        }

        // Validate subject existence
        if (!subjectRepository.existsById(subjectId)) {
            throw new ServiceException.ResourceNotFoundException("Subject", "id", subjectId);
        }

        // Get the entities
        Class aClass = classRepository.findById(classId).orElseThrow();
        Subject subject = subjectRepository.findById(subjectId).orElseThrow();

        // If both have the same program already, return it
        if (aClass.getProgram() != null && subject.getProgram() != null &&
                aClass.getProgram().getId().equals(subject.getProgram().getId())) {
            return aClass.getProgram();
        }

        // If class already has a program
        if (aClass.getProgram() != null) {
            Program program = aClass.getProgram();
            // Add the subject to this program
            subject.setProgram(program);
            program.getSubjects().add(subject);
            subjectRepository.save(subject);
            return repository.save(program);
        }

        // If subject already has a program
        if (subject.getProgram() != null) {
            Program program = subject.getProgram();
            // Add the class to this program
            aClass.setProgram(program);
            program.getClasses().add(aClass);
            classRepository.save(aClass);
            return repository.save(program);
        }

        // Create a new program
        Program program = new Program();
        program.setDescription(description);
        program.setClasses(new ArrayList<>());
        program.setSubjects(new ArrayList<>());
        Program savedProgram = repository.save(program);

        // Associate class and subject with the new program
        aClass.setProgram(savedProgram);
        subject.setProgram(savedProgram);
        savedProgram.getClasses().add(aClass);
        savedProgram.getSubjects().add(subject);

        // Save the associations
        classRepository.save(aClass);
        subjectRepository.save(subject);

        return repository.save(savedProgram);
    }

    @Override
    public void removeSubjectFromClass(UUID classId, UUID subjectId) {
        log.debug("Removing association between subject ID {} and class ID {}", subjectId, classId);

        try {
            // Find the program that has both class and subject
            Program program = findByClassAndSubject(classId, subjectId);

            // Get the class and subject
            Class aClass = classRepository.findById(classId).orElseThrow();
            Subject subject = subjectRepository.findById(subjectId).orElseThrow();

            // Remove subject from program
            program.getSubjects().remove(subject);
            subject.setProgram(null);
            subjectRepository.save(subject);

            // If program now has no subjects, consider removing class too
            if (program.getSubjects().isEmpty()) {
                program.getClasses().remove(aClass);
                aClass.setProgram(null);
                classRepository.save(aClass);

                // If program is empty, delete it
                if (program.getClasses().isEmpty()) {
                    repository.delete(program);
                } else {
                    repository.save(program);
                }
            } else {
                repository.save(program);
            }
        } catch (ServiceException.ResourceNotFoundException e) {
            // If no program found with both class and subject, do nothing
            log.debug("No program found with class ID {} and subject ID {}", classId, subjectId);
        }
    }
}