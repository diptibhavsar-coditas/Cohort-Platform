package com.example.Cohort_platform.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@Slf4j
public class JwtService {

    @Value("${cohort.jwt.secret}")
    private String secret;

    @Value("${cohort.jwt.expiry-ms}")
    private long expiryMs;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDetails user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiryMs))
                .signWith(key())
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parser().verifyWith(key()).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean isValid(String token, UserDetails user) {
        try {
            String email = extractEmail(token);
            return email.equals(user.getUsername()) && !isExpired(token);
        } catch (JwtException e) {
            log.debug("Invalid JWT: {}", e.getMessage());
            return false;
        }
    }

    private boolean isExpired(String token) {
        return Jwts.parser().verifyWith(key()).build()
                .parseSignedClaims(token).getPayload()
                .getExpiration().before(new Date());
    }
}
