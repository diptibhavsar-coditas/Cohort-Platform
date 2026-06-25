package com.example.Cohort_platform.repository;

import com.cohort.entity.Assignment;
import com.cohort.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByCourseOrderByDueAtAsc(Course course);

    @Query("""
        SELECT a FROM Assignment a
        JOIN a.course c
        JOIN c.enrollments e
        WHERE e.student.id = :studentId
        AND a.dueAt > :now
        ORDER BY a.dueAt ASC
    """)
    List<Assignment> findUpcomingForStudent(@Param("studentId") Long studentId,
                                            @Param("now") LocalDateTime now);

    @Query("""
        SELECT a FROM Assignment a
        JOIN a.course c
        JOIN c.enrollments e
        WHERE e.student.id = :studentId
        ORDER BY a.dueAt ASC
    """)
    List<Assignment> findAllForStudent(@Param("studentId") Long studentId);
}
