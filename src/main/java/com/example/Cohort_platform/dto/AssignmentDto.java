package com.example.Cohort_platform.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

public class AssignmentDto {

    @Data
    public static class CreateRequest {
        @NotBlank(message = "Title is required")
        @Size(min = 3, max = 200, message = "Title must be 3–200 characters")
        private String title;

        @Size(max = 10000, message = "Description may not exceed 10,000 characters")
        private String description;

        @NotNull(message = "Due date is required")
        @Future(message = "Due date must be in the future")
        private LocalDateTime dueAt;

        @Min(value = 1, message = "Max points must be at least 1")
        @Max(value = 1000, message = "Max points cannot exceed 1000")
        private int maxPoints = 100;
    }

    @Data
    public static class Response {
        private Long id;
        private Long courseId;
        private String courseTitle;
        private String title;
        private String description;
        private LocalDateTime dueAt;
        private int maxPoints;
        private boolean open;
        private LocalDateTime createdAt;
    }
}
