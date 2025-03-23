package com.mynotes.services;

import com.mynotes.dto.requests.AddSubjectReqDTO;
import com.mynotes.dto.responses.SubjectDTO;
import com.mynotes.models.Courses;
import com.mynotes.models.Subject;
import com.mynotes.repository.CourseRepository;
import com.mynotes.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectsRepository;

    private final CourseRepository courseRepository;

    private final AssignedTeacherService assignedTeacherService;

    private final ClassService classService;

    public void addSubject(AddSubjectReqDTO addSubjectReqDTO) {
        Subject subjects = new Subject();
        subjects.setSubjectId(addSubjectReqDTO.getSubjectId());
        subjects.setSubjectName(addSubjectReqDTO.getSubjectName());
        subjects.setSemester(addSubjectReqDTO.getSemester());
        Courses courses = courseRepository.findByCourseName(addSubjectReqDTO.getCourse().toUpperCase());
        subjects.setCourses(courses);
        subjectsRepository.save(subjects);
    }

    public List<SubjectDTO> getSubjectsByCourseId(int courseId, int classId) {
        int currentSemester = (classId == -1) ? -1 : classService.getCurrentSemester(classId);

        return subjectsRepository.findSubjectsByCourseId(courseId).stream()
                // Filter subjects only if classId is provided
                .filter(subject -> classId == -1 || subject.getSemester() == currentSemester)
                // Set assigned teacher
                .peek(subject -> {
                    if (classId == -1) {
                        subject.setAssignedTeacher(null);
                    } else {
                        subject.setAssignedTeacher(
                                assignedTeacherService.getAssignedTeacherBySubjectIdAndClassId(
                                        subject.getSubjectId(), classId
                                )
                        );
                    }
                })
                .toList(); // Or .collect(Collectors.toList()) in older Java versions
    }
}
