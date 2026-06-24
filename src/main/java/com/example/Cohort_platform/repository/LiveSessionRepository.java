package com.example.Cohort_platform.repository;

import com.example.Cohort_platform.entity.Course;
import com.example.Cohort_platform.entity.LiveSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LiveSessionRepository extends JpaRepository<LiveSession, Long> {

    List<LiveSession> findByCourse(Course course);

    List<LiveSession> findByActiveTrue();
}
