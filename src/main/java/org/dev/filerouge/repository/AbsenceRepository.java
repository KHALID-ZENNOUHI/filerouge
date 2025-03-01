package org.dev.filerouge.repository;

import org.dev.filerouge.domain.Absence;
import org.dev.filerouge.domain.Enum.AbsenceStatus;
import org.dev.filerouge.domain.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AbsenceRepository extends JpaRepository<Absence, UUID> {

    /**
     * Finds all absences for a specific student
     *
     * @param student the student
     * @return the list of absences
     */
    List<Absence> findByStudent(Student student);

    /**
     * Finds all absences for a specific student with pagination
     *
     * @param student the student
     * @param pageable the pagination information
     * @return a page of absences
     */
    Page<Absence> findByStudent(Student student, Pageable pageable);

    /**
     * Finds all absences for a specific student
     *
     * @param studentId the student ID
     * @return the list of absences
     */
    List<Absence> findByStudentId(UUID studentId);

    /**
     * Finds all absences for a specific student with pagination
     *
     * @param studentId the student ID
     * @param pageable the pagination information
     * @return a page of absences
     */
    Page<Absence> findByStudentId(UUID studentId, Pageable pageable);

    /**
     * Finds all absences for a specific class through student relationship
     *
     * @param classId the class ID
     * @return the list of absences
     */
    @Query("SELECT a FROM Absence a WHERE a.student.studentClass.id = :classId")
    List<Absence> findByStudentClassId(UUID classId);

    /**
     * Finds all absences for a specific class through student relationship with pagination
     *
     * @param classId the class ID
     * @param pageable the pagination information
     * @return a page of absences
     */
    @Query("SELECT a FROM Absence a WHERE a.student.studentClass.id = :classId")
    Page<Absence> findByStudentClassId(UUID classId, Pageable pageable);

    /**
     * Finds all absences with a specific status
     *
     * @param status the absence status
     * @return the list of absences
     */
    List<Absence> findByStatus(AbsenceStatus status);

    /**
     * Finds all absences with a specific status for a student
     *
     * @param status the absence status
     * @param studentId the student ID
     * @return the list of absences
     */
    List<Absence> findByStatusAndStudentId(AbsenceStatus status, UUID studentId);

    /**
     * Finds all absences between two dates
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return the list of absences
     */
    List<Absence> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Finds all absences between two dates for a specific student
     *
     * @param startDate the start date
     * @param endDate the end date
     * @param studentId the student ID
     * @return the list of absences
     */
    List<Absence> findByDateBetweenAndStudentId(LocalDateTime startDate, LocalDateTime endDate, UUID studentId);

    /**
     * Finds all justified absences
     *
     * @return the list of justified absences
     */
    List<Absence> findByJustificationTrue();

    /**
     * Finds all unjustified absences
     *
     * @return the list of unjustified absences
     */
    List<Absence> findByJustificationFalse();

    /**
     * Counts the number of absences for a specific student
     *
     * @param studentId the student ID
     * @return the number of absences
     */
    long countByStudentId(UUID studentId);

    /**
     * Counts the number of justified absences for a specific student
     *
     * @param studentId the student ID
     * @return the number of justified absences
     */
    long countByStudentIdAndJustificationTrue(UUID studentId);

    /**
     * Counts the number of unjustified absences for a specific student
     *
     * @param studentId the student ID
     * @return the number of unjustified absences
     */
    long countByStudentIdAndJustificationFalse(UUID studentId);

    /**
     * Deletes all absences for a specific student
     *
     * @param studentId the student ID
     */
    void deleteByStudentId(UUID studentId);
}