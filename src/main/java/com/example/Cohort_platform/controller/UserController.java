package com.example.Cohort_platform.controller;

import com.example.Cohort_platform.Enum.Role;
import com.example.Cohort_platform.dto.request.RegisterRequest;
import com.example.Cohort_platform.entity.User;
import com.example.Cohort_platform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/assignInstructor")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> assignInstructor(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.createInstructor(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a user by id")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping("/by-role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getByRole(@PathVariable Role role) {
        return ResponseEntity.ok(userService.getByRole(role));
    }

    public static class SubmissionDto {

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
}
