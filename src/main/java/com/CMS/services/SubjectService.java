package com.CMS.services;

import com.CMS.dto.requests.AddSubjectReqDTO;
import com.CMS.dto.responses.SubjectDTO;
import com.CMS.models.Courses;
import com.CMS.models.Subject;
import com.CMS.repository.CourseRepository;
import com.CMS.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectsRepository;

    private final CourseRepository courseRepository;

    private final AttendanceService attendanceService;

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

    @Transactional
    public void deleteSubject(Integer subjectId) {
        attendanceService.deleteSubjectAttendance(subjectId);
        subjectsRepository.deleteById(subjectId);
    }

    public void updateSubject(SubjectDTO subjectDTO) {
        // First find the existing subject
        Subject subject = subjectsRepository.findById(subjectDTO.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectDTO.getSubjectId()));

        // Update the fields
        subject.setSubjectId(subjectDTO.getSubjectCode());
        subject.setSubjectName(subjectDTO.getSubjectName());
        subject.setSemester(subjectDTO.getSemester());
        // Update other fields as needed

        // Save the updated subject
        subjectsRepository.save(subject);
    }
}
