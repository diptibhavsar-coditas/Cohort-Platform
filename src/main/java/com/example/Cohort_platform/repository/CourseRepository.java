package com.example.Cohort_platform.repository;

import com.example.Cohort_platform.entity.Course;
import com.example.Cohort_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByInstructor(User instructor);
    Optional<Course> findByEnrollmentCode(String code);

    @Query("SELECT c FROM Course c JOIN c.enrollments e WHERE e.student = :student")
    List<Course> findByStudent(@Param("student") User student);
}
