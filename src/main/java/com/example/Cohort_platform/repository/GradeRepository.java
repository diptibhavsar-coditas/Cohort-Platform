package com.example.Cohort_platform.repository;

import com.example.Cohort_platform.entity.Grade;
import com.example.Cohort_platform.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    Optional<Grade> findBySubmission(Submission submission);
}
