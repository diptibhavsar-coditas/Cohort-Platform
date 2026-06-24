package com.example.Cohort_platform.repository;

import com.example.Cohort_platform.entity.Assignment;
import com.example.Cohort_platform.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByCourse(Course course);

    List<Assignment> findByDueDateAfter(LocalDateTime now);
}