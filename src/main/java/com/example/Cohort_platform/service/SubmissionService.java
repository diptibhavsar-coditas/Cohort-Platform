package com.example.Cohort_platform.service;

import com.cohort.dto.SubmissionDto;
import com.cohort.entity.*;
import com.cohort.enums.SubmissionStatus;
import com.cohort.exception.BusinessException;
import com.cohort.exception.ForbiddenException;
import com.cohort.exception.ResourceNotFoundException;
import com.cohort.repository.SubmissionRepository;
import com.cohort.websocket.SubmissionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionService {

    private final SubmissionRepository submissionRepo;
    private final EmailService emailService;
    private final SimpMessagingTemplate broker;

    @Value("${cohort.upload.dir:./uploads}")
    private String uploadDir;

    @Transactional
    public Submission submit(SubmissionDto.SubmitRequest req,
                             MultipartFile file,
                             Assignment assignment,
                             User student) {

        if (!assignment.isOpen()) {
            throw new BusinessException(
                    "The deadline for \"" + assignment.getTitle() + "\" has passed. " +
                    "Submissions are no longer accepted.");
        }
        if (submissionRepo.existsByStudentAndAssignment(student, assignment)) {
            throw new BusinessException(
                    "You have already submitted work for \"" + assignment.getTitle() + "\". " +
                    "Contact your instructor if you need to resubmit.");
        }

        boolean hasText = req.getTextContent() != null && !req.getTextContent().isBlank();
        boolean hasFile = file != null && !file.isEmpty();
        if (!hasText && !hasFile) {
            throw new BusinessException(
                    "Please provide either a text response or upload a file (or both).");
        }

        Submission sub = new Submission();
        sub.setStudent(student);
        sub.setAssignment(assignment);
        sub.setTextContent(req.getTextContent());
        sub.setStatus(SubmissionStatus.SUBMITTED);
        sub.setSubmittedAt(LocalDateTime.now());

        if (hasFile) {
            String savedPath = storeFile(file,
                    assignment.getCourse().getId(), assignment.getId(), student.getId());
            sub.setFilePath(savedPath);
            sub.setOriginalFileName(file.getOriginalFilename());
        }

        submissionRepo.save(sub);

        // Broadcast live to instructor watching assignment submissions
        broker.convertAndSend(
                "/topic/assignment/" + assignment.getId() + "/submissions",
                new SubmissionEvent(sub.getId(), assignment.getId(),
                        student.getFullName(), sub.getSubmittedAt()));

        log.info("Submission {} saved — assignment {} student {}",
                sub.getId(), assignment.getId(), student.getId());
        return sub;
    }

    @Transactional
    public Submission grade(Long submissionId, SubmissionDto.GradeRequest req, User instructor) {
        Submission sub = findById(submissionId);
        Assignment a = sub.getAssignment();

        if (!a.getCourse().getInstructor().getId().equals(instructor.getId())) {
            throw new ForbiddenException("You are not the instructor for this submission.");
        }
        if (req.getPointsAwarded() > a.getMaxPoints()) {
            throw new BusinessException(
                    "Points awarded (" + req.getPointsAwarded() + ") exceed the maximum " +
                    "for this assignment (" + a.getMaxPoints() + ").");
        }

        sub.setPointsAwarded(req.getPointsAwarded());
        sub.setFeedback(req.getFeedback());
        sub.setStatus(SubmissionStatus.GRADED);
        sub.setGradedAt(LocalDateTime.now());
        sub.setGradedBy(instructor);
        submissionRepo.save(sub);

        emailService.sendGrade(
                sub.getStudent().getEmail(),
                sub.getStudent().getFullName(),
                a.getTitle(),
                sub.getPointsAwarded(),
                a.getMaxPoints(),
                sub.getFeedback());

        return sub;
    }

    @Transactional(readOnly = true)
    public List<Submission> getSubmissionsForAssignment(Assignment assignment, User instructor) {
        if (!assignment.getCourse().getInstructor().getId().equals(instructor.getId())) {
            throw new ForbiddenException("You are not the instructor for this assignment.");
        }
        return submissionRepo.findByAssignment(assignment);
    }

    @Transactional(readOnly = true)
    public List<Submission> getMySubmissions(User student) {
        return submissionRepo.findByStudent(student);
    }

    @Transactional(readOnly = true)
    public Optional<Submission> findByStudentAndAssignment(User student, Assignment assignment) {
        return submissionRepo.findByStudentAndAssignment(student, assignment);
    }

    @Transactional(readOnly = true)
    public Submission findById(Long id) {
        return submissionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found."));
    }

    private String storeFile(MultipartFile file, Long courseId, Long assignmentId, Long studentId) {
        try {
            Path dir = Paths.get(uploadDir,
                    "course-" + courseId,
                    "assignment-" + assignmentId,
                    "student-" + studentId);
            Files.createDirectories(dir);

            String original = file.getOriginalFilename();
            String ext = (original != null && original.contains("."))
                    ? original.substring(original.lastIndexOf('.')) : "";
            Path dest = dir.resolve(UUID.randomUUID() + ext);
            Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
            return dest.toString();
        } catch (IOException e) {
            log.error("File upload failed", e);
            throw new BusinessException("File upload failed. Please try again.");
        }
    }
}
