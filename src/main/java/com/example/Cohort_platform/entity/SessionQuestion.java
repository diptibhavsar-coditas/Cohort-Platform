package com.example.Cohort_platform.entity;

import com.example.Cohort_platform.Enum.QuestionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SessionQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private LiveSession session;

    @ManyToOne
    private User student;

    @Column(length = 2000)
    private String question;

    @Enumerated(EnumType.STRING)
    private QuestionStatus status; // OPEN, ANSWERED

    private LocalDateTime askedAt;
}
