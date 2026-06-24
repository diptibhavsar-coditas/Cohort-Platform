package com.example.Cohort_platform.repository;

import com.example.Cohort_platform.entity.Course;
import com.example.Cohort_platform.entity.Enrollment;
import com.example.Cohort_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudent(User student);

    List<Enrollment> findByCourse(Course course);

    boolean existsByStudentAndCourse(User student, Course course);
}
