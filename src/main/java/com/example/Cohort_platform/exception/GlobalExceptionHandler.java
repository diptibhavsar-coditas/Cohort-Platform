package com.example.Cohort_platform.exception;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@EnableWebSecurity
public class GlobalExceptionHandler {

    @ExceptionHandler(CourseNotFoundException.class)
    public
}
