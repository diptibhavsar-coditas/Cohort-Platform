package com.example.Cohort_platform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Submission submission;

    private Double marks;

    @Column(length = 2000)
    private String feedback;

    private LocalDateTime gradedAt;
}
