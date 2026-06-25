package com.example.Cohort_platform.repository;

import com.cohort.entity.Assignment;
import com.cohort.entity.Submission;
import com.cohort.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Optional<Submission> findByStudentAndAssignment(User student, Assignment assignment);
    boolean existsByStudentAndAssignment(User student, Assignment assignment);
    List<Submission> findByAssignment(Assignment assignment);
    List<Submission> findByStudent(User student);

    @Query("""
        SELECT s FROM Submission s
        WHERE s.student.id = :studentId
        AND s.assignment.course.id = :courseId
    """)
    List<Submission> findByStudentIdAndCourseId(@Param("studentId") Long studentId,
                                                 @Param("courseId") Long courseId);
}
