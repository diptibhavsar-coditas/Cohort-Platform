package com.example.Cohort_platform.repository;

import com.example.Cohort_platform.entity.StudyPlan;
import com.example.Cohort_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyPlanRepository
        extends JpaRepository<StudyPlan, Long> {

    List<StudyPlan> findByStudent(User student);
}