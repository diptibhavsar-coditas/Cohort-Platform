package com.example.Cohort_platform.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Broadcast on /topic/assignment/{assignmentId}/submissions
 * whenever a student submits — so the instructor sees it live.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionEvent {
    private Long submissionId;
    private Long assignmentId;
    private String studentName;
    private LocalDateTime submittedAt;
}
