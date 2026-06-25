package com.example.Cohort_platform.repository;

import com.cohort.entity.LiveSession;
import com.cohort.entity.SessionQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionQuestionRepository extends JpaRepository<SessionQuestion, Long> {
    List<SessionQuestion> findBySessionOrderByAskedAtAsc(LiveSession session);
}
