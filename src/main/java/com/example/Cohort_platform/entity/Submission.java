package com.example.Cohort_platform.entity;

import com.example.Cohort_platform.Enum.SubmissionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "submissions",
       uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "assignment_id"}))
@Getter @Setter @NoArgsConstructor
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    @Column(columnDefinition = "TEXT")
    private String textContent;

    private String filePath;
    private String originalFileName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus status = SubmissionStatus.SUBMITTED;

    @Column(nullable = false, updatable = false)
    private LocalDateTime submittedAt = LocalDateTime.now();

    private Integer pointsAwarded;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    private LocalDateTime gradedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graded_by")
    private User gradedBy;

    public boolean isGraded() { return status == SubmissionStatus.GRADED; }
}
