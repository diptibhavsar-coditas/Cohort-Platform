package com.example.Cohort_platform.controller;

import com.cohort.config.ResponseMapper;
import com.cohort.dto.*;
import com.cohort.entity.*;
import com.cohort.exception.ResourceNotFoundException;
import com.cohort.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final CourseService courseService;
    private final AssignmentService assignmentService;
    private final SubmissionService submissionService;
    private final LiveSessionService liveSessionService;
    private final ResponseMapper mapper;

    // ═══════════════════════════════════════════════════════
    // COURSES & ENROLMENT
    // ═══════════════════════════════════════════════════════

    /**
     * POST /api/student/enrol
     * Body: { "enrollmentCode": "ABC12345" }
     * Enrolls student and sends confirmation email.
     */
    @PostMapping("/enrol")
    public ResponseEntity<CourseDto.Response> enrol(
            @Valid @RequestBody CourseDto.EnrollRequest req,
            @AuthenticationPrincipal User student) {
        Enrollment enrollment = courseService.enroll(req.getEnrollmentCode(), student);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toCourseResponse(enrollment.getCourse()));
    }

    /**
     * GET /api/student/courses
     * List all courses the student is enrolled in.
     */
    @GetMapping("/courses")
    public ResponseEntity<List<CourseDto.Response>> myCourses(
            @AuthenticationPrincipal User student) {
        return ResponseEntity.ok(
                mapper.toCourseList(courseService.getEnrolledCourses(student)));
    }

    /**
     * GET /api/student/courses/{courseId}
     * Get a single course the student is enrolled in.
     */
    @GetMapping("/courses/{courseId}")
    public ResponseEntity<CourseDto.Response> getCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User student) {
        Course course = courseService.getCourseAsStudent(courseId, student);
        return ResponseEntity.ok(mapper.toCourseResponse(course));
    }

    // ═══════════════════════════════════════════════════════
    // ASSIGNMENTS
    // ═══════════════════════════════════════════════════════

    /**
     * GET /api/student/assignments/upcoming
     * All open assignments across all enrolled courses, sorted by due date.
     */
    @GetMapping("/assignments/upcoming")
    public ResponseEntity<List<AssignmentDto.Response>> upcomingAssignments(
            @AuthenticationPrincipal User student) {
        return ResponseEntity.ok(
                mapper.toAssignmentList(assignmentService.getUpcomingForStudent(student)));
    }

    /**
     * GET /api/student/assignments
     * All assignments across all enrolled courses (including past).
     */
    @GetMapping("/assignments")
    public ResponseEntity<List<AssignmentDto.Response>> allAssignments(
            @AuthenticationPrincipal User student) {
        return ResponseEntity.ok(
                mapper.toAssignmentList(assignmentService.getAllForStudent(student)));
    }

    /**
     * GET /api/student/courses/{courseId}/assignments
     * Assignments for a specific course.
     */
    @GetMapping("/courses/{courseId}/assignments")
    public ResponseEntity<List<AssignmentDto.Response>> courseAssignments(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User student) {
        Course course = courseService.getCourseAsStudent(courseId, student);
        return ResponseEntity.ok(
                mapper.toAssignmentList(assignmentService.getAssignmentsForCourse(course)));
    }

    /**
     * GET /api/student/assignments/{assignmentId}
     * Get a single assignment (must be enrolled in its course).
     */
    @GetMapping("/assignments/{assignmentId}")
    public ResponseEntity<AssignmentDto.Response> getAssignment(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal User student) {
        Assignment a = assignmentService.getAssignmentAsStudent(assignmentId, student);
        return ResponseEntity.ok(mapper.toAssignmentResponse(a));
    }

    // ═══════════════════════════════════════════════════════
    // SUBMISSIONS
    // ═══════════════════════════════════════════════════════

    /**
     * POST /api/student/assignments/{assignmentId}/submit
     * Multipart form: textContent (optional text) + file (optional file).
     * At least one must be present. Broadcasts live to instructor's feed.
     */
    @PostMapping("/assignments/{assignmentId}/submit")
    public ResponseEntity<SubmissionDto.Response> submit(
            @PathVariable Long assignmentId,
            @RequestPart(value = "data") @Valid SubmissionDto.SubmitRequest req,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal User student) {
        Assignment a = assignmentService.getAssignmentAsStudent(assignmentId, student);
        Submission sub = submissionService.submit(req, file, a, student);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toSubmissionResponse(sub));
    }

    /**
     * GET /api/student/assignments/{assignmentId}/submission
     * Get this student's submission for a given assignment (if it exists).
     */
    @GetMapping("/assignments/{assignmentId}/submission")
    public ResponseEntity<SubmissionDto.Response> mySubmission(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal User student) {
        Assignment a = assignmentService.getAssignmentAsStudent(assignmentId, student);
        Submission sub = submissionService.findByStudentAndAssignment(student, a)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No submission found for this assignment."));
        return ResponseEntity.ok(mapper.toSubmissionResponse(sub));
    }

    /**
     * GET /api/student/submissions
     * All of this student's submissions across all courses (shows grades too).
     */
    @GetMapping("/submissions")
    public ResponseEntity<List<SubmissionDto.Response>> mySubmissions(
            @AuthenticationPrincipal User student) {
        return ResponseEntity.ok(
                mapper.toSubmissionList(submissionService.getMySubmissions(student)));
    }

    // ═══════════════════════════════════════════════════════
    // LIVE SESSIONS
    // ═══════════════════════════════════════════════════════

    /**
     * GET /api/student/courses/{courseId}/sessions
     * List sessions for an enrolled course.
     */
    @GetMapping("/courses/{courseId}/sessions")
    public ResponseEntity<List<SessionDto.Response>> listSessions(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User student) {
        Course course = courseService.getCourseAsStudent(courseId, student);
        List<SessionDto.Response> sessions = liveSessionService
                .getSessionsForCourse(course).stream()
                .map(s -> mapper.toSessionResponse(s, false))
                .toList();
        return ResponseEntity.ok(sessions);
    }

    /**
     * GET /api/student/sessions/{sessionId}
     * Get a session with all current questions (initial load).
     * Then subscribe to /topic/session/{id} for live updates.
     */
    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<SessionDto.Response> getSession(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal User student) {
        LiveSession session = liveSessionService.getSessionAsStudent(sessionId, student);
        return ResponseEntity.ok(mapper.toSessionResponse(session, true));
    }

    /**
     * POST /api/student/sessions/{sessionId}/ask
     * Body: { "text": "Can you explain that again?" }
     * Broadcasts QUESTION_ASKED to /topic/session/{id} immediately.
     */
    @PostMapping("/sessions/{sessionId}/ask")
    public ResponseEntity<SessionDto.QuestionResponse> askQuestion(
            @PathVariable Long sessionId,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal User student) {
        String text = body.get("text");
        SessionQuestion q = liveSessionService.askQuestion(sessionId, text, student);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toQuestionResponse(q));
    }
}
