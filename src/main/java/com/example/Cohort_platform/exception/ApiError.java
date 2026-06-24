package com.example.Cohort_platform.exception;


import jakarta.persistence.GeneratedValue;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApiError {

    private String message;
    private LocalDateTime timestamp;
    private String

}
