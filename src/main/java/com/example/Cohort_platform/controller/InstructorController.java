package com.example.Cohort_platform.controller;

import com.example.Cohort_platform.config.ResponseMapper;
import com.example.Cohort_platform.dto.CourseDto;
import com.example.Cohort_platform.entity.Course;
import com.example.Cohort_platform.entity.User;
import com.example.Cohort_platform.service.AssignmentService;
import com.example.Cohort_platform.service.CourseService;
import com.example.Cohort_platform.service.LiveSessionService;
import com.example.Cohort_platform.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instructor")
@RequiredArgsConstructor
public class InstructorController {

    private final CourseService courseService;
    private final AssignmentService assignmentService;
    private final SubmissionService submissionService;
    private final LiveSessionService liveSessionService;
    private final ResponseMapper mapper;


    // COURSES
    /**
     * POST /api/instructor/courses
     * Create a new course. Returns the course including its enrollment code.
     */
    @PostMapping("/courses")
    public ResponseEntity<CourseDto.Response> createCourse(
            @Valid @RequestBody CourseDto.CreateRequest req,
            @AuthenticationPrincipal User instructor) {
        Course course = courseService.createCourse(req, instructor);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toCourseResponse(course));
    }

    /**
     * GET /api/instructor/courses
     * List all courses this instructor teaches.
     */
    @GetMapping("/courses")
    public ResponseEntity<List<CourseDto.Response>> myCourses(
            @AuthenticationPrincipal User instructor) {
        return ResponseEntity.ok(mapper.toCourseList(courseService.getMyCourses(instructor)));
    }

    /**
     * GET /api/instructor/courses/{courseId}
     * Get a single course (must be yours).
     */
    @GetMapping("/courses/{courseId}")
    public ResponseEntity<CourseDto.Response> getCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User instructor) {
        Course course = courseService.getCourseAsInstructor(courseId, instructor);
        return ResponseEntity.ok(mapper.toCourseResponse(course));
    }

    // ASSIGNMENTS
    /**
     * POST /api/instructor/courses/{courseId}/assignments
     * Create an assignment and email all enrolled students.
     */
    @PostMapping("/courses/{courseId}/assignments")
    public ResponseEntity<AssignmentDto.Response> createAssignment(
            @PathVariable Long courseId,
            @Valid @RequestBody AssignmentDto.CreateRequest req,
            @AuthenticationPrincipal User instructor) {
        Course course = courseService.getCourseAsInstructor(courseId, instructor);
        Assignment a = assignmentService.createAssignment(req, course);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toAssignmentResponse(a));
    }

    /**
     * GET /api/instructor/courses/{courseId}/assignments
     * List all assignments for a course, ordered by due date.
     */
    @GetMapping("/courses/{courseId}/assignments")
    public ResponseEntity<List<AssignmentDto.Response>> listAssignments(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User instructor) {
        Course course = courseService.getCourseAsInstructor(courseId, instructor);
        return ResponseEntity.ok(
                mapper.toAssignmentList(assignmentService.getAssignmentsForCourse(course)));
    }

    /**
     * GET /api/instructor/assignments/{assignmentId}
     * Get a single assignment.
     */
    @GetMapping("/assignments/{assignmentId}")
    public ResponseEntity<AssignmentDto.Response> getAssignment(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal User instructor) {
        Assignment a = assignmentService.getAssignmentAsInstructor(assignmentId, instructor);
        return ResponseEntity.ok(mapper.toAssignmentResponse(a));
    }


    // SUBMISSIONS & GRADING

    /**
     * GET /api/instructor/assignments/{assignmentId}/submissions
     * List all submissions for an assignment.
     * Subscribing to /topic/assignment/{id}/submissions gives live updates.
     */
    @GetMapping("/assignments/{assignmentId}/submissions")
    public ResponseEntity<List<SubmissionDto.Response>> listSubmissions(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal User instructor) {
        Assignment a = assignmentService.getAssignmentAsInstructor(assignmentId, instructor);
        return ResponseEntity.ok(mapper.toSubmissionList(
                submissionService.getSubmissionsForAssignment(a, instructor)));
    }

    /**
     * GET /api/instructor/submissions/{submissionId}
     * Get a single submission to review before grading.
     */
    @GetMapping("/submissions/{submissionId}")
    public ResponseEntity<SubmissionDto.Response> getSubmission(
            @PathVariable Long submissionId,
            @AuthenticationPrincipal User instructor) {
        Submission sub = submissionService.findById(submissionId);
        // access check — will throw ForbiddenException if not the instructor
        assignmentService.getAssignmentAsInstructor(sub.getAssignment().getId(), instructor);
        return ResponseEntity.ok(mapper.toSubmissionResponse(sub));
    }

    /**
     * POST /api/instructor/submissions/{submissionId}/grade
     * Body: { "pointsAwarded": 85, "feedback": "Great work!" }
     * Emails the student and returns the updated submission.
     */
    @PostMapping("/submissions/{submissionId}/grade")
    public ResponseEntity<SubmissionDto.Response> grade(
            @PathVariable Long submissionId,
            @Valid @RequestBody SubmissionDto.GradeRequest req,
            @AuthenticationPrincipal User instructor) {
        Submission sub = submissionService.grade(submissionId, req, instructor);
        return ResponseEntity.ok(mapper.toSubmissionResponse(sub));
    }

    // LIVE SESSIONS
    /**
     * POST /api/instructor/courses/{courseId}/sessions
     * Create a new live session (starts closed).
     */
    @PostMapping("/courses/{courseId}/sessions")
    public ResponseEntity<SessionDto.Response> createSession(
            @PathVariable Long courseId,
            @Valid @RequestBody SessionDto.CreateRequest req,
            @AuthenticationPrincipal User instructor) {
        Course course = courseService.getCourseAsInstructor(courseId, instructor);
        LiveSession session = liveSessionService.createSession(req, course);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toSessionResponse(session, false));
    }

    /**
     * GET /api/instructor/courses/{courseId}/sessions
     * List all sessions for a course.
     */
    @GetMapping("/courses/{courseId}/sessions")
    public ResponseEntity<List<SessionDto.Response>> listSessions(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User instructor) {
        Course course = courseService.getCourseAsInstructor(courseId, instructor);
        List<SessionDto.Response> sessions = liveSessionService
                .getSessionsForCourse(course).stream()
                .map(s -> mapper.toSessionResponse(s, false))
                .toList();
        return ResponseEntity.ok(sessions);
    }

    /**
     * GET /api/instructor/sessions/{sessionId}
     * Get a session with all its questions.
     */
    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<SessionDto.Response> getSession(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal User instructor) {
        LiveSession session = liveSessionService.getSessionAsInstructor(sessionId, instructor);
        return ResponseEntity.ok(mapper.toSessionResponse(session, true));
    }

    /**
     * POST /api/instructor/sessions/{sessionId}/open
     * Open a session — broadcasts SESSION_OPENED to /topic/session/{id}.
     */
    @PostMapping("/sessions/{sessionId}/open")
    public ResponseEntity<SessionDto.Response> openSession(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal User instructor) {
        LiveSession session = liveSessionService.openSession(sessionId, instructor);
        return ResponseEntity.ok(mapper.toSessionResponse(session, false));
    }

    /**
     * POST /api/instructor/sessions/{sessionId}/close
     * Close a session — broadcasts SESSION_CLOSED to /topic/session/{id}.
     */
    @PostMapping("/sessions/{sessionId}/close")
    public ResponseEntity<SessionDto.Response> closeSession(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal User instructor) {
        LiveSession session = liveSessionService.closeSession(sessionId, instructor);
        return ResponseEntity.ok(mapper.toSessionResponse(session, false));
    }

    /**
     * POST /api/instructor/questions/{questionId}/answer
     * Mark a question answered — broadcasts QUESTION_ANSWERED to /topic/session/{id}.
     */
    @PostMapping("/questions/{questionId}/answer")
    public ResponseEntity<SessionDto.QuestionResponse> markAnswered(
            @PathVariable Long questionId,
            @AuthenticationPrincipal User instructor) {
        SessionQuestion q = liveSessionService.markAnswered(questionId, instructor);
        return ResponseEntity.ok(mapper.toQuestionResponse(q));
    }

    /**
     * GET /api/instructor/sessions/{sessionId}/questions
     * Fetch all questions for a session (for initial load; live updates via WebSocket).
     */
    @GetMapping("/sessions/{sessionId}/questions")
    public ResponseEntity<List<SessionDto.QuestionResponse>> getQuestions(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal User instructor) {
        liveSessionService.getSessionAsInstructor(sessionId, instructor); // auth check
        List<SessionDto.QuestionResponse> questions = liveSessionService
                .getQuestions(sessionId).stream()
                .map(mapper::toQuestionResponse)
                .toList();
        return ResponseEntity.ok(questions);
    }
}
