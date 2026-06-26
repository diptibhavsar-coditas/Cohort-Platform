package com.example.Cohort_platform.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionEvent {

    private String type;
    private Long sessionId;
    private Long questionId;
    private String askerName;
    private String text;
    private LocalDateTime timestamp;

    public static SessionEvent sessionOpened(Long sessionId) {
        SessionEvent e = new SessionEvent();
        e.setType("SESSION_OPENED");
        e.setSessionId(sessionId);
        e.setTimestamp(LocalDateTime.now());
        return e;
    }

    public static SessionEvent sessionClosed(Long sessionId) {
        SessionEvent e = new SessionEvent();
        e.setType("SESSION_CLOSED");
        e.setSessionId(sessionId);
        e.setTimestamp(LocalDateTime.now());
        return e;
    }

    public static SessionEvent questionAsked(Long questionId, Long sessionId,
                                              String askerName, String text,
                                              LocalDateTime askedAt) {
        SessionEvent e = new SessionEvent();
        e.setType("QUESTION_ASKED");
        e.setQuestionId(questionId);
        e.setSessionId(sessionId);
        e.setAskerName(askerName);
        e.setText(text);
        e.setTimestamp(askedAt);
        return e;
    }

    public static SessionEvent questionAnswered(Long questionId, Long sessionId,
                                                 LocalDateTime answeredAt) {
        SessionEvent e = new SessionEvent();
        e.setType("QUESTION_ANSWERED");
        e.setQuestionId(questionId);
        e.setSessionId(sessionId);
        e.setTimestamp(answeredAt);
        return e;
    }
}
