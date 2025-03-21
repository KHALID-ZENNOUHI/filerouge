package org.dev.filerouge.service.implementation;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.dev.filerouge.domain.Program;
import org.dev.filerouge.domain.Subject;
import org.dev.filerouge.web.error.ServiceException;
import org.dev.filerouge.repository.ClassRepository;
import org.dev.filerouge.repository.ProgramRepository;
import org.dev.filerouge.repository.SubjectRepository;
import org.dev.filerouge.repository.TeacherRepository;
import org.dev.filerouge.service.ISubjectService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of {@link ISubjectService} for managing {@link Subject} entities.
 */
@Service
@Transactional
@Slf4j
public class SubjectService extends BaseServiceImpl<Subject, SubjectRepository> implements ISubjectService {

    private final ClassRepository classRepository;
    private final TeacherRepository teacherRepository;
    private final ProgramRepository programRepository;

    public SubjectService(SubjectRepository subjectRepository,
                          ClassRepository classRepository,
                          TeacherRepository teacherRepository,
                          ProgramRepository programRepository) {
        super(subjectRepository, "Subject");
        this.classRepository = classRepository;
        this.teacherRepository = teacherRepository;
        this.programRepository = programRepository;
    }

    @Override
    public Subject save(Subject subject) {
        log.debug("Saving subject: {}", subject);

        // Validate subject
        validateSubject(subject);

        // Validate name uniqueness
        if (existsByName(subject.getName())) {
            throw new ServiceException.DuplicateResourceException("Subject", "name", subject.getName());
        }

        return super.save(subject);
    }

    @Override
    public Subject update(Subject subject) {
        log.debug("Updating subject: {}", subject);

        // Check if subject exists
        if (!existsById(subject.getId())) {
            throw new ServiceException.ResourceNotFoundException("Subject", "id", subject.getId());
        }

        // Validate subject
        validateSubject(subject);

        // Validate name uniqueness (only if name is changed)
        Subject existingSubject = findById(subject.getId());
        if (!existingSubject.getName().equals(subject.getName()) && existsByName(subject.getName())) {
            throw new ServiceException.DuplicateResourceException("Subject", "name", subject.getName());
        }

        // Preserve program relationship
        if (subject.getProgram() == null && existingSubject.getProgram() != null) {
            subject.setProgram(existingSubject.getProgram());
        }

        return super.update(subject);
    }

    @Override
    public Subject findByName(String name) {
        log.debug("Finding subject by name: {}", name);

        if (name == null || name.trim().isEmpty()) {
            throw new ServiceException.ValidationException("Subject name cannot be empty");
        }

        return repository.findByName(name)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("Subject", "name", name));
    }

    @Override
    public boolean existsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return repository.existsByName(name);
    }

    @Override
    public List<Subject> findByClassId(UUID classId) {
        log.debug("Finding subjects by class ID: {}", classId);

        // Validate class existence
        if (!classRepository.existsById(classId)) {
            throw new ServiceException.ResourceNotFoundException("Class", "id", classId);
        }

        return repository.findByProgramClassId(classId);
    }

    @Override
    public List<Subject> findByProgramId(UUID programId) {
        log.debug("Finding subjects by program ID: {}", programId);

        // Validate program existence
        if (!programRepository.existsById(programId)) {
            throw new ServiceException.ResourceNotFoundException("Program", "id", programId);
        }

        return repository.findByProgramId(programId);
    }

    @Override
    public List<Subject> searchByName(String searchTerm) {
        log.debug("Searching subjects by name: {}", searchTerm);

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new ServiceException.ValidationException("Search term cannot be empty");
        }

        return repository.findByNameContainingIgnoreCase(searchTerm.trim());
    }

    @Override
    public Subject assignToProgram(UUID subjectId, UUID programId) {
        log.debug("Assigning subject ID {} to program ID {}", subjectId, programId);

        // Validate subject existence
        Subject subject = findById(subjectId);

        // Validate program existence
        if (!programRepository.existsById(programId)) {
            throw new ServiceException.ResourceNotFoundException("Program", "id", programId);
        }
        Program program = programRepository.getReferenceById(programId);

        // Check if subject is already assigned to this program
        if (subject.getProgram() != null && subject.getProgram().getId().equals(programId)) {
            return subject; // Already assigned
        }

        // If subject is already assigned to another program, remove from that program first
        if (subject.getProgram() != null) {
            // Remove the subject from its current program's subjects list
            Program currentProgram = subject.getProgram();
            currentProgram.getSubjects().remove(subject);
            programRepository.save(currentProgram);
        }

        // Assign subject to new program
        subject.setProgram(program);
        program.getSubjects().add(subject);

        // Save both entities
        programRepository.save(program);
        return repository.save(subject);
    }

    @Override
    public Subject removeFromProgram(UUID subjectId) {
        log.debug("Removing subject ID {} from its program", subjectId);

        // Validate subject existence
        Subject subject = findById(subjectId);

        // If subject is not assigned to a program, nothing to do
        if (subject.getProgram() == null) {
            return subject;
        }

        // Remove the subject from its program's subjects list
        Program program = subject.getProgram();
        program.getSubjects().remove(subject);

        // Clear the program reference from the subject
        subject.setProgram(null);

        // Save both entities
        programRepository.save(program);
        return repository.save(subject);
    }

    /**
     * Validates a subject entity
     *
     * @param subject the subject to validate
     * @throws ServiceException.ValidationException if validation fails
     */
    private void validateSubject(Subject subject) {
        if (subject.getName() == null || subject.getName().trim().isEmpty()) {
            throw new ServiceException.ValidationException("Subject name cannot be empty");
        }
    }
}