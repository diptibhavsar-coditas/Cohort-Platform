package com.example.Cohort_platform.dto;

import com.cohort.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

// ── Auth ──────────────────────────────────────────────────────

public class AuthDto {

    @Data
    public static class RegisterRequest {
        @NotBlank(message = "Full name is required")
        @Size(min = 2, max = 100, message = "Name must be 2–100 characters")
        private String fullName;

        @NotBlank(message = "Email is required")
        @Email(message = "Must be a valid email address")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        private String password;

        @NotNull(message = "Role is required")
        private Role role;
    }

    @Data
    public static class LoginRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Must be a valid email address")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;
    }

    @Data
    public static class TokenResponse {
        private String token;
        private String type = "Bearer";
        private Long userId;
        private String fullName;
        private String email;
        private Role role;

        public TokenResponse(String token, Long userId, String fullName, String email, Role role) {
            this.token = token;
            this.userId = userId;
            this.fullName = fullName;
            this.email = email;
            this.role = role;
        }
    }
}
