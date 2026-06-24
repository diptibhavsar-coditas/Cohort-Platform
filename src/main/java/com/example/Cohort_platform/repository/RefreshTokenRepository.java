package com.example.Cohort_platform.repository;

import com.example.Cohort_platform.entity.RefreshToken;
import com.example.Cohort_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface RefreshTokenRepository
            extends JpaRepository<RefreshToken, Long> {

        Optional<RefreshToken> findByToken(String token);

        Optional<RefreshToken> findByUser(User user);

        void deleteByUser(User user);
    }