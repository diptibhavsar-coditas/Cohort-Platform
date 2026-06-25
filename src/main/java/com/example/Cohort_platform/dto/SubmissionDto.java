package com.example.Cohort_platform.dto;

import com.cohort.enums.SubmissionStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

public class SubmissionDto {

    @Data
    public static class SubmitRequest {
        @Size(max = 20000, message = "Text content may not exceed 20,000 characters")
        private String textContent;
        // file is handled separately via MultipartFile
    }

    @Data
    public static class GradeRequest {
        @NotNull(message = "Points awarded is required")
        @Min(value = 0, message = "Points cannot be negative")
        private Integer pointsAwarded;

        @Size(max = 5000, message = "Feedback may not exceed 5000 characters")
        private String feedback;
    }

    @Data
    public static class Response {
        private Long id;
        private Long assignmentId;
        private String assignmentTitle;
        private Long studentId;
        private String studentName;
        private String textContent;
        private String originalFileName;
        private SubmissionStatus status;
        private LocalDateTime submittedAt;
        private Integer pointsAwarded;
        private Integer maxPoints;
        private String feedback;
        private LocalDateTime gradedAt;
    }
}
