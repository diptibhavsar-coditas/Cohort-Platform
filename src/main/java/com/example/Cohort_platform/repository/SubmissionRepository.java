package com.example.Cohort_platform.repository;

import com.example.Cohort_platform.entity.Assignment;
import com.example.Cohort_platform.entity.Submission;
import com.example.Cohort_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    List<Submission> findByStudent(User student);

    List<Submission> findByAssignment(Assignment assignment);

    Optional<Submission> findByStudentAndAssignment(User student,
                                                    Assignment assignment);
}