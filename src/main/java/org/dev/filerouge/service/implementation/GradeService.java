package org.dev.filerouge.service.implementation;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.dev.filerouge.domain.Activity;
import org.dev.filerouge.domain.Grade;
import org.dev.filerouge.domain.Student;
import org.dev.filerouge.repository.ActivityRepository;
import org.dev.filerouge.repository.GradeRepository;
import org.dev.filerouge.repository.StudentRepository;
import org.dev.filerouge.service.IGradeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.dev.filerouge.web.error.ServiceException;

import java.util.List;
import java.util.UUID;
import java.util.OptionalDouble;

/**
 * Implementation of {@link IGradeService} for managing {@link Grade} entities.
 */
@Service
@Transactional
@Slf4j
public class GradeService extends BaseServiceImpl<Grade, GradeRepository> implements IGradeService {

    private final StudentRepository studentRepository;
    private final ActivityRepository activityRepository;

    public GradeService(GradeRepository gradeRepository,
                        StudentRepository studentRepository,
                        ActivityRepository activityRepository) {
        super(gradeRepository, "Grade");
        this.studentRepository = studentRepository;
        this.activityRepository = activityRepository;
    }

    @Override
    public Grade save(Grade grade) {
        log.debug("Saving grade: {}", grade);

        // Validate grade
        validateGrade(grade);

        // Validate student existence
        UUID studentId = grade.getStudent().getId();
        if (!studentRepository.existsById(studentId)) {
            throw new ServiceException.ResourceNotFoundException("Student", "id", studentId);
        }

        // Validate activity existence
        UUID activityId = grade.getActivity().getId();
        if (!activityRepository.existsById(activityId)) {
            throw new ServiceException.ResourceNotFoundException("Activity", "id", activityId);
        }

        return super.save(grade);
    }

    @Override
    public Grade update(Grade grade) {
        log.debug("Updating grade: {}", grade);

        // Check if grade exists
        if (!existsById(grade.getId())) {
            throw new ServiceException.ResourceNotFoundException("Grade", "id", grade.getId());
        }

        // Validate grade
        validateGrade(grade);

        // Validate student existence
        UUID studentId = grade.getStudent().getId();
        if (!studentRepository.existsById(studentId)) {
            throw new ServiceException.ResourceNotFoundException("Student", "id", studentId);
        }

        // Validate activity existence
        UUID activityId = grade.getActivity().getId();
        if (!activityRepository.existsById(activityId)) {
            throw new ServiceException.ResourceNotFoundException("Activity", "id", activityId);
        }

        return super.update(grade);
    }

    @Override
    public List<Grade> findByStudentId(UUID studentId) {
        log.debug("Finding grades by student ID: {}", studentId);

        // Validate student existence
        if (!studentRepository.existsById(studentId)) {
            throw new ServiceException.ResourceNotFoundException("Student", "id", studentId);
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("Student", "id", studentId));

        return repository.findByStudent(student);
    }

    @Override
    public Page<Grade> findByStudentId(UUID studentId, int page, int size) {
        log.debug("Finding grades by student ID with pagination: {}", studentId);

        // Validate student existence
        if (!studentRepository.existsById(studentId)) {
            throw new ServiceException.ResourceNotFoundException("Student", "id", studentId);
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("Student", "id", studentId));

        return repository.findByStudent(student, PageRequest.of(page, size));
    }

    @Override
    public List<Grade> findByActivityId(UUID activityId) {
        log.debug("Finding grades by activity ID: {}", activityId);

        // Validate activity existence
        if (!activityRepository.existsById(activityId)) {
            throw new ServiceException.ResourceNotFoundException("Activity", "id", activityId);
        }

        return repository.findByActivityId(activityId);
    }

    @Override
    public Float calculateStudentAverage(UUID studentId) {
        log.debug("Calculating average grade for student ID: {}", studentId);

        // Validate student existence
        if (!studentRepository.existsById(studentId)) {
            throw new ServiceException.ResourceNotFoundException("Student", "id", studentId);
        }

        List<Grade> grades = findByStudentId(studentId);

        if (grades.isEmpty()) {
            return null;
        }

        OptionalDouble average = grades.stream()
                .filter(grade -> grade.getGrade() != null)
                .mapToDouble(grade -> grade.getGrade())
                .average();

        return average.isPresent() ? (float) average.getAsDouble() : null;
    }

    @Override
    public Float calculateActivityAverage(UUID activityId) {
        log.debug("Calculating average grade for activity ID: {}", activityId);

        // Validate activity existence
        if (!activityRepository.existsById(activityId)) {
            throw new ServiceException.ResourceNotFoundException("Activity", "id", activityId);
        }

        List<Grade> grades = findByActivityId(activityId);

        if (grades.isEmpty()) {
            return null;
        }

        OptionalDouble average = grades.stream()
                .filter(grade -> grade.getGrade() != null)
                .mapToDouble(grade -> grade.getGrade())
                .average();

        return average.isPresent() ? (float) average.getAsDouble() : null;
    }

    @Override
    public void deleteByStudentId(UUID studentId) {
        log.debug("Deleting all grades for student ID: {}", studentId);

        // Validate student existence
        if (!studentRepository.existsById(studentId)) {
            throw new ServiceException.ResourceNotFoundException("Student", "id", studentId);
        }

        repository.deleteByStudentId(studentId);
    }

    @Override
    public void deleteByActivityId(UUID activityId) {
        log.debug("Deleting all grades for activity ID: {}", activityId);

        // Validate activity existence
        if (!activityRepository.existsById(activityId)) {
            throw new ServiceException.ResourceNotFoundException("Activity", "id", activityId);
        }

        repository.deleteByActivityId(activityId);
    }

    /**
     * Validates a grade entity
     *
     * @param grade the grade to validate
     * @throws ServiceException.ValidationException if validation fails
     */
    private void validateGrade(Grade grade) {
        if (grade.getGrade() == null) {
            throw new ServiceException.ValidationException("Grade value cannot be null");
        }

        if (grade.getGrade() < 0 || grade.getGrade() > 20) {
            throw new ServiceException.ValidationException("Grade value must be between 0 and 20");
        }

        if (grade.getStudent() == null || grade.getStudent().getId() == null) {
            throw new ServiceException.ValidationException("Student is required");
        }

        if (grade.getActivity() == null || grade.getActivity().getId() == null) {
            throw new ServiceException.ValidationException("Activity is required");
        }
    }
}