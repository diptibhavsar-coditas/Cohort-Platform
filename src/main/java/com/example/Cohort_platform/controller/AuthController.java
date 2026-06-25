package com.example.Cohort_platform.controller;

import com.example.Cohort_platform.dto.AuthDto;
import com.example.Cohort_platform.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/register
     * Body: { "fullName", "email", "password", "role": "STUDENT"|"INSTRUCTOR" }
     * Returns: JWT token + user summary
     */
    @PostMapping("/register")
    public ResponseEntity<AuthDto.TokenResponse> register(
            @Valid @RequestBody AuthDto.RegisterRequest req) {
        AuthDto.TokenResponse response = authService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/auth/login
     * Body: { "email", "password" }
     * Returns: JWT token + user summary
     */
    @PostMapping("/login")
    public ResponseEntity<AuthDto.TokenResponse> login(
            @Valid @RequestBody AuthDto.LoginRequest req) {
        AuthDto.TokenResponse response = authService.login(req);
        return ResponseEntity.ok(response);
    }
}
