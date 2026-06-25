package com.example.Cohort_platform.service;

import com.cohort.dto.CourseDto;
import com.cohort.entity.Course;
import com.cohort.entity.Enrollment;
import com.cohort.entity.User;
import com.cohort.exception.BusinessException;
import com.cohort.exception.ForbiddenException;
import com.cohort.exception.ResourceNotFoundException;
import com.cohort.repository.CourseRepository;
import com.cohort.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final EmailService emailService;

    // ── Instructor operations ─────────────────────────────

    @Transactional
    public Course createCourse(CourseDto.CreateRequest req, User instructor) {
        Course course = new Course();
        course.setTitle(req.getTitle().strip());
        course.setDescription(req.getDescription());
        course.setCapacity(req.getCapacity());
        course.setInstructor(instructor);
        course.setEnrollmentCode(generateCode());
        return courseRepo.save(course);
    }

    @Transactional(readOnly = true)
    public List<Course> getMyCourses(User instructor) {
        return courseRepo.findByInstructor(instructor);
    }

    @Transactional(readOnly = true)
    public Course getCourseAsInstructor(Long courseId, User instructor) {
        Course course = findById(courseId);
        if (!course.getInstructor().getId().equals(instructor.getId())) {
            throw new ForbiddenException("You are not the instructor of this course.");
        }
        return course;
    }

    // ── Student operations ────────────────────────────────

    @Transactional
    public Enrollment enroll(String code, User student) {
        Course course = courseRepo.findByEnrollmentCode(code.strip().toUpperCase())
                .orElseThrow(() -> new BusinessException(
                        "No course found with that enrollment code. Please double-check and try again."));

        if (!course.isActive()) {
            throw new BusinessException(
                    "\"" + course.getTitle() + "\" is no longer accepting enrolments.");
        }
        if (enrollmentRepo.existsByStudentAndCourse(student, course)) {
            throw new BusinessException(
                    "You are already enrolled in \"" + course.getTitle() + "\".");
        }
        if (course.isFull()) {
            throw new BusinessException(
                    "\"" + course.getTitle() + "\" is full (" + course.getCapacity() +
                    " students). Contact your instructor for assistance.");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollmentRepo.save(enrollment);

        emailService.sendEnrollmentConfirmation(
                student.getEmail(), student.getFullName(), course.getTitle());

        return enrollment;
    }

    @Transactional(readOnly = true)
    public List<Course> getEnrolledCourses(User student) {
        return courseRepo.findByStudent(student);
    }

    @Transactional(readOnly = true)
    public Course getCourseAsStudent(Long courseId, User student) {
        Course course = findById(courseId);
        if (!enrollmentRepo.existsByStudentAndCourse(student, course)) {
            throw new ForbiddenException("You are not enrolled in this course.");
        }
        return course;
    }

    // ── Shared ────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Course findById(Long courseId) {
        return courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found."));
    }

    private String generateCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().replace("-", "")
                       .substring(0, 8).toUpperCase();
        } while (courseRepo.findByEnrollmentCode(code).isPresent());
        return code;
    }
}
