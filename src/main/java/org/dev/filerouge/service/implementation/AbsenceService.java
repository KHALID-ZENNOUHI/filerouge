package org.dev.filerouge.service.implementation;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.dev.filerouge.domain.Absence;
import org.dev.filerouge.domain.Enum.AbsenceStatus;
import org.dev.filerouge.domain.Student;
import org.dev.filerouge.web.error.ServiceException;
import org.dev.filerouge.repository.AbsenceRepository;
import org.dev.filerouge.repository.ClassRepository;
import org.dev.filerouge.repository.StudentRepository;
import org.dev.filerouge.service.IAbsenceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of {@link IAbsenceService} for managing {@link Absence} entities.
 */
@Service
@Transactional
@Slf4j
public class AbsenceService extends BaseServiceImpl<Absence, AbsenceRepository> implements IAbsenceService {

    private final StudentRepository studentRepository;
    private final ClassRepository classRepository;

    public AbsenceService(AbsenceRepository absenceRepository,
                          StudentRepository studentRepository,
                          ClassRepository classRepository) {
        super(absenceRepository, "Absence");
        this.studentRepository = studentRepository;
        this.classRepository = classRepository;
    }

    @Override
    public Absence save(Absence absence) {
        log.debug("Saving absence: {}", absence);

        // Validate absence
        validateAbsence(absence);

        // Validate student existence
        if (absence.getStudent() != null && absence.getStudent().getId() != null) {
            UUID studentId = absence.getStudent().getId();
            if (!studentRepository.existsById(studentId)) {
                throw new ServiceException.ResourceNotFoundException("Student", "id", studentId);
            }
        } else {
            throw new ServiceException.ValidationException("Student is required for an absence");
        }

        return super.save(absence);
    }

    @Override
    public Absence update(Absence absence) {
        log.debug("Updating absence: {}", absence);

        // Check if absence exists
        if (!existsById(absence.getId())) {
            throw new ServiceException.ResourceNotFoundException("Absence", "id", absence.getId());
        }

        // Validate absence
        validateAbsence(absence);

        // Validate student existence
        if (absence.getStudent() != null && absence.getStudent().getId() != null) {
            UUID studentId = absence.getStudent().getId();
            if (!studentRepository.existsById(studentId)) {
                throw new ServiceException.ResourceNotFoundException("Student", "id", studentId);
            }
        } else {
            throw new ServiceException.ValidationException("Student is required for an absence");
        }

        return super.update(absence);
    }

    @Override
    public List<Absence> findByStudentId(UUID studentId) {
        log.debug("Finding absences by student ID: {}", studentId);

        // Validate student existence
        if (!studentRepository.existsById(studentId)) {
            throw new ServiceException.ResourceNotFoundException("Student", "id", studentId);
        }

        return repository.findByStudentId(studentId);
    }

    @Override
    public Page<Absence> findByStudentId(UUID studentId, int page, int size) {
        log.debug("Finding absences by student ID with pagination: {}", studentId);

        // Validate student existence
        if (!studentRepository.existsById(studentId)) {
            throw new ServiceException.ResourceNotFoundException("Student", "id", studentId);
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("Student", "id", studentId));

        return repository.findByStudent(student, PageRequest.of(page, size));
    }

    @Override
    public List<Absence> findByClassId(UUID classId) {
        log.debug("Finding absences by class ID: {}", classId);

        // Validate class existence
        if (!classRepository.existsById(classId)) {
            throw new ServiceException.ResourceNotFoundException("Class", "id", classId);
        }

        return repository.findByStudentClassId(classId);
    }

    @Override
    public Page<Absence> findByClassId(UUID classId, int page, int size) {
        log.debug("Finding absences by class ID with pagination: {}", classId);

        // Validate class existence
        if (!classRepository.existsById(classId)) {
            throw new ServiceException.ResourceNotFoundException("Class", "id", classId);
        }

        return repository.findByStudentClassId(classId, PageRequest.of(page, size));
    }

    @Override
    public List<Absence> findByStatus(AbsenceStatus status) {
        log.debug("Finding absences by status: {}", status);

        if (status == null) {
            throw new ServiceException.ValidationException("Absence status cannot be null");
        }

        return repository.findByStatus(status);
    }

    @Override
    public List<Absence> findByStatusAndStudentId(AbsenceStatus status, UUID studentId) {
        log.debug("Finding absences by status and student ID: {}, {}", status, studentId);

        if (status == null) {
            throw new ServiceException.ValidationException("Absence status cannot be null");
        }

        // Validate student existence
        if (!studentRepository.existsById(studentId)) {
            throw new ServiceException.ResourceNotFoundException("Student", "id", studentId);
        }

        return repository.findByStatusAndStudentId(status, studentId);
    }

    @Override
    public List<Absence> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Finding absences between dates: {} and {}", startDate, endDate);

        if (startDate == null || endDate == null) {
            throw new ServiceException.ValidationException("Start date and end date cannot be null");
        }

        if (startDate.isAfter(endDate)) {
            throw new ServiceException.ValidationException("Start date cannot be after end date");
        }

        return repository.findByDateBetween(startDate, endDate);
    }

    @Override
    public List<Absence> findByDateRangeAndStudentId(LocalDateTime startDate, LocalDateTime endDate, UUID studentId) {
        log.debug("Finding absences between dates for student ID: {} and {}, {}", startDate, endDate, studentId);

        if (startDate == null || endDate == null) {
            throw new ServiceException.ValidationException("Start date and end date cannot be null");
        }

        if (startDate.isAfter(endDate)) {
            throw new ServiceException.ValidationException("Start date cannot be after end date");
        }

        // Validate student existence
        if (!studentRepository.existsById(studentId)) {
            throw new ServiceException.ResourceNotFoundException("Student", "id", studentId);
        }

        return repository.findByDateBetweenAndStudentId(startDate, endDate, studentId);
    }

    @Override
    public List<Absence> findJustifiedAbsences() {
        log.debug("Finding justified absences");
        return repository.findByJustificationTrue();
    }

    @Override
    public List<Absence> findUnjustifiedAbsences() {
        log.debug("Finding unjustified absences");
        return repository.findByJustificationFalse();
    }

    @Override
    public Absence justifyAbsence(UUID absenceId, String justificationText) {
        log.debug("Justifying absence: {}", absenceId);

        Absence absence = findById(absenceId);
        absence.setJustification(true);
        absence.setJustificationText(justificationText);

        return update(absence);
    }

    @Override
    public Absence markAsUnjustified(UUID absenceId) {
        log.debug("Marking absence as unjustified: {}", absenceId);

        Absence absence = findById(absenceId);
        absence.setJustification(false);
        absence.setJustificationText(null);

        return update(absence);
    }

    @Override
    public Absence changeStatus(UUID absenceId, AbsenceStatus status) {
        log.debug("Changing absence status: {}, {}", absenceId, status);

        if (status == null) {
            throw new ServiceException.ValidationException("Absence status cannot be null");
        }

        Absence absence = findById(absenceId);
        absence.setStatus(status);

        return update(absence);
    }

    @Override
    public Map<String, Object> getStudentAbsenceStatistics(UUID studentId) {
        log.debug("Getting absence statistics for student ID: {}", studentId);

        // Validate student existence
        if (!studentRepository.existsById(studentId)) {
            throw new ServiceException.ResourceNotFoundException("Student", "id", studentId);
        }

        Map<String, Object> statistics = new HashMap<>();

        // Get student information
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("Student", "id", studentId));

        statistics.put("studentName", student.getFirstName() + " " + student.getLastName());

        if (student.getStudentClass() != null) {
            statistics.put("className", student.getStudentClass().getName());
        }

        long totalAbsences = countByStudentId(studentId);
        long justifiedAbsences = countJustifiedByStudentId(studentId);
        long unjustifiedAbsences = countUnjustifiedByStudentId(studentId);

        statistics.put("totalAbsences", totalAbsences);
        statistics.put("justifiedAbsences", justifiedAbsences);
        statistics.put("unjustifiedAbsences", unjustifiedAbsences);

        // Calculate percentage of justified absences
        double justifiedPercentage = totalAbsences > 0 ?
                (double) justifiedAbsences / totalAbsences * 100 : 0;
        statistics.put("justifiedPercentage", Math.round(justifiedPercentage * 100.0) / 100.0);

        // Calculate percentage of unjustified absences
        double unjustifiedPercentage = totalAbsences > 0 ?
                (double) unjustifiedAbsences / totalAbsences * 100 : 0;
        statistics.put("unjustifiedPercentage", Math.round(unjustifiedPercentage * 100.0) / 100.0);

        // Get absences by status
        List<Absence> absences = findByStudentId(studentId);
        Map<AbsenceStatus, Long> absencesByStatus = absences.stream()
                .collect(Collectors.groupingBy(Absence::getStatus, Collectors.counting()));
        statistics.put("absencesByStatus", absencesByStatus);

        return statistics;
    }

    @Override
    public Map<String, Object> getClassAbsenceStatistics(UUID classId) {
        log.debug("Getting absence statistics for class ID: {}", classId);

        // Validate class existence
        if (!classRepository.existsById(classId)) {
            throw new ServiceException.ResourceNotFoundException("Class", "id", classId);
        }

        Map<String, Object> statistics = new HashMap<>();

        // Get class information
        org.dev.filerouge.domain.Class aClass = classRepository.findById(classId)
                .orElseThrow(() -> new ServiceException.ResourceNotFoundException("Class", "id", classId));

        statistics.put("className", aClass.getName());

        if (aClass.getLevel() != null) {
            statistics.put("levelName", aClass.getLevel().getName());
        }

        // Get all students in the class
        List<Student> students = studentRepository.findByStudentClassId(classId);
        statistics.put("totalStudents", students.size());

        // Get all absences for the class
        List<Absence> absences = findByClassId(classId);
        statistics.put("totalAbsences", absences.size());

        // Calculate average absences per student
        double averageAbsences = students.size() > 0 ?
                (double) absences.size() / students.size() : 0;
        statistics.put("averageAbsencesPerStudent", Math.round(averageAbsences * 100.0) / 100.0);

        // Count justified and unjustified absences
        long justifiedAbsences = absences.stream()
                .filter(Absence::isJustification)
                .count();
        long unjustifiedAbsences = absences.size() - justifiedAbsences;

        statistics.put("justifiedAbsences", justifiedAbsences);
        statistics.put("unjustifiedAbsences", unjustifiedAbsences);

        // Calculate percentage of justified absences
        double justifiedPercentage = absences.size() > 0 ?
                (double) justifiedAbsences / absences.size() * 100 : 0;
        statistics.put("justifiedPercentage", Math.round(justifiedPercentage * 100.0) / 100.0);

        // Calculate percentage of unjustified absences
        double unjustifiedPercentage = absences.size() > 0 ?
                (double) unjustifiedAbsences / absences.size() * 100 : 0;
        statistics.put("unjustifiedPercentage", Math.round(unjustifiedPercentage * 100.0) / 100.0);

        // Get absences by status
        Map<AbsenceStatus, Long> absencesByStatus = absences.stream()
                .collect(Collectors.groupingBy(Absence::getStatus, Collectors.counting()));
        statistics.put("absencesByStatus", absencesByStatus);

        // Get students with most absences
        Map<String, Long> absencesByStudent = absences.stream()
                .collect(Collectors.groupingBy(
                        absence -> absence.getStudent().getFirstName() + " " + absence.getStudent().getLastName(),
                        Collectors.counting()
                ));

        // Sort by number of absences (descending) and limit to top 5
        List<Map.Entry<String, Long>> topAbsentStudents = absencesByStudent.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());

        statistics.put("topAbsentStudents", topAbsentStudents);

        return statistics;
    }

    @Override
    public long countByStudentId(UUID studentId) {
        log.debug("Counting absences for student ID: {}", studentId);

        // Validate student existence
        if (!studentRepository.existsById(studentId)) {
            throw new ServiceException.ResourceNotFoundException("Student", "id", studentId);
        }

        return repository.countByStudentId(studentId);
    }

    @Override
    public long countJustifiedByStudentId(UUID studentId) {
        log.debug("Counting justified absences for student ID: {}", studentId);

        // Validate student existence
        if (!studentRepository.existsById(studentId)) {
            throw new ServiceException.ResourceNotFoundException("Student", "id", studentId);
        }

        return repository.countByStudentIdAndJustificationTrue(studentId);
    }

    @Override
    public long countUnjustifiedByStudentId(UUID studentId) {
        log.debug("Counting unjustified absences for student ID: {}", studentId);

        // Validate student existence
        if (!studentRepository.existsById(studentId)) {
            throw new ServiceException.ResourceNotFoundException("Student", "id", studentId);
        }

        return repository.countByStudentIdAndJustificationFalse(studentId);
    }

    @Override
    public void deleteByStudentId(UUID studentId) {
        log.debug("Deleting all absences for student ID: {}", studentId);

        // Validate student existence
        if (!studentRepository.existsById(studentId)) {
            throw new ServiceException.ResourceNotFoundException("Student", "id", studentId);
        }

        repository.deleteByStudentId(studentId);
    }

    /**
     * Validates an absence entity
     *
     * @param absence the absence to validate
     * @throws ServiceException.ValidationException if validation fails
     */
    private void validateAbsence(Absence absence) {
        if (absence.getDate() == null) {
            throw new ServiceException.ValidationException("Absence date cannot be null");
        }

        if (absence.getStatus() == null) {
            throw new ServiceException.ValidationException("Absence status cannot be null");
        }

        if (absence.isJustification() &&
                (absence.getJustificationText() == null || absence.getJustificationText().trim().isEmpty())) {
            throw new ServiceException.ValidationException("Justification text is required for justified absences");
        }

        if (absence.getStudent() == null || absence.getStudent().getId() == null) {
            throw new ServiceException.ValidationException("Student is required for an absence");
        }
    }
}