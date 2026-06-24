package com.example.Cohort_platform.repository;

import com.example.Cohort_platform.entity.Course;
import com.example.Cohort_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByInstructor(User instructor);
}
