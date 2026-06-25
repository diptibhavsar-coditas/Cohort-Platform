package com.example.Cohort_platform.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class CourseDto {

    @Data
    public static class CreateRequest {
        @NotBlank(message = "Title is required")
        @Size(min = 3, max = 200, message = "Title must be 3–200 characters")
        private String title;

        @Size(max = 5000, message = "Description may not exceed 5000 characters")
        private String description;

        @Min(value = 1, message = "Capacity must be at least 1")
        @Max(value = 500, message = "Capacity cannot exceed 500")
        private int capacity = 30;
    }

    @Data
    public static class EnrollRequest {
        @NotBlank(message = "Enrollment code is required")
        private String enrollmentCode;
    }

    @Data
    public static class Response {
        private Long id;
        private String title;
        private String description;
        private int capacity;
        private int enrolledCount;
        private boolean active;
        private String enrollmentCode;
        private InstructorSummary instructor;
        private String createdAt;
    }

    @Data
    public static class InstructorSummary {
        private Long id;
        private String fullName;
        private String email;
    }
}
