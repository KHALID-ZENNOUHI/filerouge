package org.dev.filerouge.repository;

import org.dev.filerouge.domain.Grade;
import org.dev.filerouge.domain.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GradeRepository extends JpaRepository<Grade, UUID> {

    /**
     * Finds all grades for a specific student
     *
     * @param student the student
     * @return the list of grades
     */
    List<Grade> findByStudent(Student student);

    /**
     * Finds all grades for a specific student with pagination
     *
     * @param student the student
     * @param pageable the pagination information
     * @return a page of grades
     */
    Page<Grade> findByStudent(Student student, Pageable pageable);

    /**
     * Finds all grades for a specific activity
     *
     * @param activityId the activity ID
     * @return the list of grades
     */
    List<Grade> findByActivityId(UUID activityId);

    /**
     * Deletes all grades for a specific student
     *
     * @param studentId the student ID
     */
    void deleteByStudentId(UUID studentId);

    /**
     * Deletes all grades for a specific activity
     *
     * @param activityId the activity ID
     */
    void deleteByActivityId(UUID activityId);
}