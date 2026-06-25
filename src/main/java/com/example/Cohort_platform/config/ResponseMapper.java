package com.example.Cohort_platform.config;

import com.example.Cohort_platform.dto.AssignmentDto;
import com.example.Cohort_platform.dto.CourseDto;
import com.example.Cohort_platform.dto.SessionDto;
import com.example.Cohort_platform.dto.SubmissionDto;
import com.example.Cohort_platform.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Central entity → DTO mapper. Keeps controllers thin.
 */
@Component
public class ResponseMapper {

    //courses
    public CourseDto.Response toCourseResponse(Course c) {
        CourseDto.Response r = new CourseDto.Response();
        r.setId(c.getId());
        r.setTitle(c.getTitle());
        r.setDescription(c.getDescription());
        r.setCapacity(c.getCapacity());
        r.setEnrolledCount(c.enrolledCount());
        r.setActive(c.isActive());
        r.setEnrollmentCode(c.getEnrollmentCode());
        r.setCreatedAt(c.getCreatedAt().toString());

        CourseDto.InstructorSummary ins = new CourseDto.InstructorSummary();
        ins.setId(c.getInstructor().getId());
        ins.setFullName(c.getInstructor().getFullName());
        ins.setEmail(c.getInstructor().getEmail());
        r.setInstructor(ins);

        return r;
    }

    public List<CourseDto.Response> toCourseList(List<Course> courses) {
        return courses.stream().map(this::toCourseResponse).toList();
    }

    //assignment
    public AssignmentDto.Response toAssignmentResponse(Assignment a) {
        AssignmentDto.Response r = new AssignmentDto.Response();
        r.setId(a.getId());
        r.setCourseId(a.getCourse().getId());
        r.setCourseTitle(a.getCourse().getTitle());
        r.setTitle(a.getTitle());
        r.setDescription(a.getDescription());
        r.setDueAt(a.getDueAt());
        r.setMaxPoints(a.getMaxPoints());
        r.setOpen(a.isOpen());
        r.setCreatedAt(a.getCreatedAt());
        return r;
    }

    public List<AssignmentDto.Response> toAssignmentList(List<Assignment> assignments) {
        return assignments.stream().map(this::toAssignmentResponse).toList();
    }

    //submission

    public SubmissionDto.Response toSubmissionResponse(Submission s) {
        SubmissionDto.Response r = new SubmissionDto.Response();
        r.setId(s.getId());
        r.setAssignmentId(s.getAssignment().getId());
        r.setAssignmentTitle(s.getAssignment().getTitle());
        r.setStudentId(s.getStudent().getId());
        r.setStudentName(s.getStudent().getFullName());
        r.setTextContent(s.getTextContent());
        r.setOriginalFileName(s.getOriginalFileName());
        r.setStatus(s.getStatus());
        r.setSubmittedAt(s.getSubmittedAt());
        r.setPointsAwarded(s.getPointsAwarded());
        r.setMaxPoints(s.getAssignment().getMaxPoints());
        r.setFeedback(s.getFeedback());
        r.setGradedAt(s.getGradedAt());
        return r;
    }

    public List<SubmissionDto.Response> toSubmissionList(List<Submission> subs) {
        return subs.stream().map(this::toSubmissionResponse).toList();
    }

    //Session

    public SessionDto.Response toSessionResponse(LiveSession s, boolean includeQuestions) {
        SessionDto.Response r = new SessionDto.Response();
        r.setId(s.getId());
        r.setCourseId(s.getCourse().getId());
        r.setCourseTitle(s.getCourse().getTitle());
        r.setTitle(s.getTitle());
        r.setOpen(s.isOpen());
        r.setCreatedAt(s.getCreatedAt());
        r.setOpenedAt(s.getOpenedAt());
        r.setClosedAt(s.getClosedAt());
        if (includeQuestions) {
            r.setQuestions(s.getQuestions().stream()
                    .map(this::toQuestionResponse).toList());
        }
        return r;
    }

    public SessionDto.QuestionResponse toQuestionResponse(SessionQuestion q) {
        SessionDto.QuestionResponse r = new SessionDto.QuestionResponse();
        r.setId(q.getId());
        r.setSessionId(q.getSession().getId());
        r.setAskerId(q.getAsker().getId());
        r.setAskerName(q.getAsker().getFullName());
        r.setText(q.getText());
        r.setAnswered(q.isAnswered());
        r.setAskedAt(q.getAskedAt());
        r.setAnsweredAt(q.getAnsweredAt());
        return r;
    }
}
