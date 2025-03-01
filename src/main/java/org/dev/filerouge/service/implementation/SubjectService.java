package org.dev.filerouge.service.implementation;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.dev.filerouge.domain.Subject;
import org.dev.filerouge.web.error.ServiceException;
import org.dev.filerouge.repository.ClassRepository;
import org.dev.filerouge.repository.SubjectRepository;
import org.dev.filerouge.repository.TeacherRepository;
import org.dev.filerouge.service.ISubjectService;
import org.dev.filerouge.service.implementation.BaseServiceImpl;
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

    public SubjectService(SubjectRepository subjectRepository,
                          ClassRepository classRepository,
                          TeacherRepository teacherRepository) {
        super(subjectRepository, "Subject");
        this.classRepository = classRepository;
        this.teacherRepository = teacherRepository;
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

        return repository.findByProgramsClassesId(classId);
    }

    @Override
    public List<Subject> findByTeacherId(UUID teacherId) {
        log.debug("Finding subjects by teacher ID: {}", teacherId);

        // Validate teacher existence
        if (!teacherRepository.existsById(teacherId)) {
            throw new ServiceException.ResourceNotFoundException("Teacher", "id", teacherId);
        }

        return repository.findBySessionsTeacherId(teacherId);
    }

    @Override
    public List<Subject> searchByName(String searchTerm) {
        log.debug("Searching subjects by name: {}", searchTerm);

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new ServiceException.ValidationException("Search term cannot be empty");
        }

        return repository.findByNameContainingIgnoreCase(searchTerm.trim());
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