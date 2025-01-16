package com.mynotes.services;

import com.mynotes.dto.requests.AssignTeacherDTO;
import com.mynotes.enums.Role;
import com.mynotes.models.*;
import com.mynotes.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssignedTeacherService {

    private final AssignedTeacherRepository assignedTeacherRepository;

    private final SubjectRepository subjectRepository;

    private final CourseRepository courseRepository;

    private final ClassRepository classRepository;

    private final TeacherDetailsRepository teacherRepository;

    private final UserRepository userRepository;

    public void assignTeacherToSubject(AssignTeacherDTO assignTeacherDTO) {
        Courses course = courseRepository.findByCourseName(assignTeacherDTO.getCourseName().toUpperCase());
        if (course == null) {
            throw new IllegalArgumentException("Course not found: " + assignTeacherDTO.getCourseName());
        }
        Subject subject = subjectRepository.findBySubjectId(assignTeacherDTO.getSubjectId());
        if (subject == null) {
            throw new IllegalArgumentException("Subject not found: " + assignTeacherDTO.getSubjectId());
        }
        ClassEntity classEntity = classRepository.findBySectionAndBatchYearAndCourse(assignTeacherDTO.getSection(), assignTeacherDTO.getBatchYear(), course);
        if (classEntity == null) {
            throw new IllegalArgumentException("Class not found: " + assignTeacherDTO.getSection() + " " + assignTeacherDTO.getBatchYear() + " " + assignTeacherDTO.getCourseName());
        }
        TeacherDetails teacherDetails = teacherRepository.findTeacherDetailsByUucmsId(assignTeacherDTO.getTeacherId());
        if (teacherDetails == null) {
            throw new IllegalArgumentException("Teacher not found: " + assignTeacherDTO.getTeacherId());
        }
        AssignedTeacher assignedTeacher = new AssignedTeacher();;
        assignedTeacher.setSubject(subject);
        assignedTeacher.setClassEntity(classEntity);
        assignedTeacher.setTeacher(teacherDetails);
        assignedTeacherRepository.save(assignedTeacher);
    }
}
