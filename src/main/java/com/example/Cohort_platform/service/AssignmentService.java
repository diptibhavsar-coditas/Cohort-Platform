package com.example.Cohort_platform.service;


import com.example.Cohort_platform.repository.AssignmentRepository;
import com.example.Cohort_platform.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final EmailService emailService;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("EEE d MMM yyyy 'at' HH:mm");

    @Transactional
    public Assignment createAssignment(AssignmentDto.CreateRequest req, Course course) {
        Assignment a = new Assignment();
        a.setTitle(req.getTitle().strip());
        a.setDescription(req.getDescription());
        a.setDueAt(req.getDueAt());
        a.setMaxPoints(req.getMaxPoints());
        a.setCourse(course);
        assignmentRepo.save(a);

        // Notify all enrolled students asynchronously
        List<Enrollment> enrollments = enrollmentRepo.findByCourse(course);
        for (Enrollment e : enrollments) {
            emailService.sendNewAssignment(
                    e.getStudent().getEmail(),
                    e.getStudent().getFullName(),
                    course.getTitle(),
                    a.getTitle(),
                    a.getDueAt().format(FMT));
        }
        return a;
    }

    @Transactional(readOnly = true)
    public List<Assignment> getAssignmentsForCourse(Course course) {
        return assignmentRepo.findByCourseOrderByDueAtAsc(course);
    }

    @Transactional(readOnly = true)
    public Assignment getAssignmentAsInstructor(Long assignmentId, User instructor) {
        Assignment a = findById(assignmentId);
        if (!a.getCourse().getInstructor().getId().equals(instructor.getId())) {
            throw new ForbiddenException("You are not the instructor of this assignment's course.");
        }
        return a;
    }

    @Transactional(readOnly = true)
    public Assignment getAssignmentAsStudent(Long assignmentId, User student) {
        Assignment a = findById(assignmentId);
        if (!enrollmentRepo.existsByStudentAndCourse(student, a.getCourse())) {
            throw new ForbiddenException("You are not enrolled in this assignment's course.");
        }
        return a;
    }

    @Transactional(readOnly = true)
    public List<Assignment> getUpcomingForStudent(User student) {
        return assignmentRepo.findUpcomingForStudent(student.getId(), LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<Assignment> getAllForStudent(User student) {
        return assignmentRepo.findAllForStudent(student.getId());
    }

    @Transactional(readOnly = true)
    public Assignment findById(Long id) {
        return assignmentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found."));
    }
}
