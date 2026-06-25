package com.example.Cohort_platform.service;

import com.cohort.dto.AuthDto;
import com.cohort.entity.User;
import com.cohort.exception.BusinessException;
import com.cohort.repository.UserRepository;
import com.cohort.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final EmailService emailService;

    @Transactional
    public AuthDto.TokenResponse register(AuthDto.RegisterRequest req) {
        if (userRepo.existsByEmail(req.getEmail().toLowerCase().strip())) {
            throw new BusinessException("An account with that email already exists.");
        }

        User user = new User();
        user.setEmail(req.getEmail().toLowerCase().strip());
        user.setFullName(req.getFullName().strip());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setRole(req.getRole());
        userRepo.save(user);

        emailService.sendWelcome(user.getEmail(), user.getFullName());

        String token = jwtService.generateToken(user);
        return new AuthDto.TokenResponse(token, user.getId(),
                user.getFullName(), user.getEmail(), user.getRole());
    }

    public AuthDto.TokenResponse login(AuthDto.LoginRequest req) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            req.getEmail().toLowerCase().strip(),
                            req.getPassword()));
        } catch (AuthenticationException e) {
            throw new BusinessException("Incorrect email or password.");
        }

        User user = userRepo.findByEmail(req.getEmail().toLowerCase().strip())
                .orElseThrow(() -> new BusinessException("User not found."));

        String token = jwtService.generateToken(user);
        return new AuthDto.TokenResponse(token, user.getId(),
                user.getFullName(), user.getEmail(), user.getRole());
    }
}
