package com.example.Cohort_platform.repository;

import com.example.Cohort_platform.Enum.QuestionStatus;
import com.example.Cohort_platform.entity.LiveSession;
import com.example.Cohort_platform.entity.SessionQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionQuestionRepository
        extends JpaRepository<SessionQuestion, Long> {

    List<SessionQuestion> findBySession(LiveSession session);

    List<SessionQuestion> findBySessionAndStatus(LiveSession session,
                                                 QuestionStatus status);
}
