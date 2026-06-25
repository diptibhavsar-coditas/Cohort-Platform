package com.example.Cohort_platform.controller;


import com.example.Cohort_platform.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    /**
     * GET /api/me
     * Returns the currently authenticated user's profile.
     * Useful for clients to confirm their token is valid and get their role.
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(Map.of(
                "id",       user.getId(),
                "fullName", user.getFullName(),
                "email",    user.getEmail(),
                "role",     user.getRole()
        ));
    }
}
