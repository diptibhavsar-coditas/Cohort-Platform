package com.example.Cohort_platform.repository;

import com.cohort.entity.Course;
import com.cohort.entity.LiveSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LiveSessionRepository extends JpaRepository<LiveSession, Long> {
    List<LiveSession> findByCourseOrderByCreatedAtDesc(Course course);
    Optional<LiveSession> findByCourseAndOpenTrue(Course course);
}
