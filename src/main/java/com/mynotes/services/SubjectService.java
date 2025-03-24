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

    public List<SubjectDTO> getSubjectsByCourseId(int courseId) {
        return subjectsRepository.findSubjectsByCourseId(courseId);
    }
    public List<SubjectDTO> getSubjectsOfAClass(int classId,int courseId) {
        int currentSemester = classService.getCurrentSemester(classId);
        System.out.println(currentSemester);
        return subjectsRepository.findSubjectsOfAClass(courseId, currentSemester).stream()
                .peek( subject ->
                        subject.setAssignedTeacher(
                        assignedTeacherService.getAssignedTeacherBySubjectIdAndClassId(
                                subject.getSubjectId(), classId
                        )
                        )
                ).toList();
    }
}
