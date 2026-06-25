package com.example.Cohort_platform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${cohort.mail.dry-run:true}")
    private boolean dryRun;

    @Value("${cohort.mail.from:Cohort <no-reply@cohort.app>}")
    private String from;

    @Async
    public void send(String to, String subject, String body) {
        if (dryRun) {
            log.info("📧 [DRY-RUN] To={} | Subject={} | Body={}", to, subject, body);
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(from);
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);
            log.info("Email sent → {} : {}", to, subject);
        } catch (Exception e) {
            log.error("Failed sending email to {}: {}", to, e.getMessage());
        }
    }

    public void sendWelcome(String to, String name) {
        send(to, "Welcome to Cohort",
            "Hi " + name + ",\n\nYour Cohort account is ready. Sign in to get started.\n\n— Cohort");
    }

    public void sendEnrollmentConfirmation(String to, String name, String courseTitle) {
        send(to, "Enrolled: " + courseTitle,
            "Hi " + name + ",\n\nYou've successfully enrolled in " + courseTitle + ".\n\n— Cohort");
    }

    public void sendNewAssignment(String to, String name, String courseTitle,
                                  String title, String dueAt) {
        send(to, "[" + courseTitle + "] New assignment: " + title,
            "Hi " + name + ",\n\nA new assignment has been posted:\n\n" +
            "  " + title + "\n  Due: " + dueAt + "\n\nLog in to view and submit.\n\n— Cohort");
    }

    public void sendGrade(String to, String name, String title,
                          int points, int max, String feedback) {
        send(to, "Graded: " + title,
            "Hi " + name + ",\n\nYour submission for " + title + " has been graded.\n\n" +
            "Score: " + points + " / " + max + "\n\n" +
            (feedback != null && !feedback.isBlank() ? "Feedback:\n" + feedback + "\n\n" : "") +
            "— Cohort");
    }
}
