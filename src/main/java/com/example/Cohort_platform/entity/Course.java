package com.example.Cohort_platform.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String courseName ;

    private String courseDuration;

    private String assignment;



    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private User instructor;

    @OneToMany
    @JoinColumn(name = "student_id")
    private User student;

}
