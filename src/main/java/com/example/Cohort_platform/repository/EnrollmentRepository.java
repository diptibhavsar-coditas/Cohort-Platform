package com.example.Cohort_platform.repository;

import com.example.Cohort_platform.entity.Enrollment;
import com.example.Cohort_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByStudentAndCourse(User student, Course course);
    boolean existsByStudentAndCourse(User student, Course course);
    List<Enrollment> findByCourse(Course course);
    List<Enrollment> findByStudent(User student);
}
