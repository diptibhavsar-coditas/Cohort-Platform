package com.example.Cohort_platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class SessionDto {

    @Data
    public static class CreateRequest {
        @NotBlank(message = "Session title is required")
        @Size(min = 3, max = 200, message = "Title must be 3–200 characters")
        private String title;
    }

    @Data
    public static class Response {
        private Long id;
        private Long courseId;
        private String courseTitle;
        private String title;
        private boolean open;
        private LocalDateTime createdAt;
        private LocalDateTime openedAt;
        private LocalDateTime closedAt;
        private List<QuestionResponse> questions;
    }

    @Data
    public static class QuestionResponse {
        private Long id;
        private Long sessionId;
        private Long askerId;
        private String askerName;
        private String text;
        private boolean answered;
        private LocalDateTime askedAt;
        private LocalDateTime answeredAt;
    }
}
