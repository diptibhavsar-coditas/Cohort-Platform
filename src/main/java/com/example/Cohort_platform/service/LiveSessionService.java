package com.example.Cohort_platform.service;

import com.cohort.dto.SessionDto;
import com.cohort.entity.*;
import com.cohort.exception.BusinessException;
import com.cohort.exception.ForbiddenException;
import com.cohort.exception.ResourceNotFoundException;
import com.cohort.repository.*;
import com.cohort.websocket.SessionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LiveSessionService {

    private final LiveSessionRepository sessionRepo;
    private final SessionQuestionRepository questionRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final SimpMessagingTemplate broker;

    // ── Session lifecycle ─────────────────────────────────

    @Transactional
    public LiveSession createSession(SessionDto.CreateRequest req, Course course) {
        LiveSession s = new LiveSession();
        s.setTitle(req.getTitle().strip());
        s.setCourse(course);
        return sessionRepo.save(s);
    }

    @Transactional
    public LiveSession openSession(Long sessionId, User instructor) {
        LiveSession session = getSessionAsInstructor(sessionId, instructor);
        if (session.isOpen()) {
            throw new BusinessException("Session is already open.");
        }
        // Close any other open session for this course first
        sessionRepo.findByCourseAndOpenTrue(session.getCourse()).ifPresent(other -> {
            if (!other.getId().equals(sessionId)) {
                other.setOpen(false);
                other.setClosedAt(LocalDateTime.now());
                sessionRepo.save(other);
            }
        });

        session.setOpen(true);
        session.setOpenedAt(LocalDateTime.now());
        sessionRepo.save(session);

        broadcast(sessionId, SessionEvent.sessionOpened(sessionId));
        return session;
    }

    @Transactional
    public LiveSession closeSession(Long sessionId, User instructor) {
        LiveSession session = getSessionAsInstructor(sessionId, instructor);
        if (!session.isOpen()) {
            throw new BusinessException("Session is not currently open.");
        }
        session.setOpen(false);
        session.setClosedAt(LocalDateTime.now());
        sessionRepo.save(session);

        broadcast(sessionId, SessionEvent.sessionClosed(sessionId));
        return session;
    }

    // ── Questions ─────────────────────────────────────────

    @Transactional
    public SessionQuestion askQuestion(Long sessionId, String text, User student) {
        LiveSession session = findById(sessionId);

        if (!session.isOpen()) {
            throw new BusinessException("This session is not currently open for questions.");
        }
        if (!enrollmentRepo.existsByStudentAndCourse(student, session.getCourse())) {
            throw new ForbiddenException("You are not enrolled in this session's course.");
        }
        if (text == null || text.isBlank()) {
            throw new BusinessException("Question text cannot be blank.");
        }

        SessionQuestion q = new SessionQuestion();
        q.setSession(session);
        q.setAsker(student);
        q.setText(text.strip());
        q.setAskedAt(LocalDateTime.now());
        questionRepo.save(q);

        broadcast(sessionId, SessionEvent.questionAsked(
                q.getId(), sessionId, student.getFullName(), q.getText(), q.getAskedAt()));

        return q;
    }

    @Transactional
    public SessionQuestion markAnswered(Long questionId, User instructor) {
        SessionQuestion q = questionRepo.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found."));

        LiveSession session = q.getSession();
        if (!session.getCourse().getInstructor().getId().equals(instructor.getId())) {
            throw new ForbiddenException("Only the instructor can mark questions as answered.");
        }
        if (q.isAnswered()) {
            throw new BusinessException("This question has already been marked answered.");
        }

        q.setAnswered(true);
        q.setAnsweredAt(LocalDateTime.now());
        questionRepo.save(q);

        broadcast(session.getId(), SessionEvent.questionAnswered(
                q.getId(), session.getId(), q.getAnsweredAt()));

        return q;
    }

    // ── Queries ───────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<LiveSession> getSessionsForCourse(Course course) {
        return sessionRepo.findByCourseOrderByCreatedAtDesc(course);
    }

    @Transactional(readOnly = true)
    public LiveSession getSessionAsInstructor(Long sessionId, User instructor) {
        LiveSession session = findById(sessionId);
        if (!session.getCourse().getInstructor().getId().equals(instructor.getId())) {
            throw new ForbiddenException("You are not the instructor for this session.");
        }
        return session;
    }

    @Transactional(readOnly = true)
    public LiveSession getSessionAsStudent(Long sessionId, User student) {
        LiveSession session = findById(sessionId);
        if (!enrollmentRepo.existsByStudentAndCourse(student, session.getCourse())) {
            throw new ForbiddenException("You are not enrolled in this session's course.");
        }
        return session;
    }

    @Transactional(readOnly = true)
    public List<SessionQuestion> getQuestions(Long sessionId) {
        LiveSession session = findById(sessionId);
        return questionRepo.findBySessionOrderByAskedAtAsc(session);
    }

    @Transactional(readOnly = true)
    public LiveSession findById(Long id) {
        return sessionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found."));
    }

    private void broadcast(Long sessionId, Object event) {
        broker.convertAndSend("/topic/session/" + sessionId, event);
    }
}
