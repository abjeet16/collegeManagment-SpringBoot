package com.mynotes.services;

import com.mynotes.dto.requests.AssignTeacherDTO;
import com.mynotes.enums.Role;
import com.mynotes.models.*;
import com.mynotes.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        // Fetch Subject
        Subject subject = subjectRepository.findBySubjectId(assignTeacherDTO.getSubjectId());
        if (subject == null) {
            throw new IllegalArgumentException("Subject not found: " + assignTeacherDTO.getSubjectId());
        }

        // Fetch ClassEntity
        ClassEntity classEntity = classRepository.findById(assignTeacherDTO.getClassId())
                .orElseThrow(() -> new IllegalArgumentException("Class not found: " + assignTeacherDTO.getClassId()));

        // Fetch TeacherDetails
        TeacherDetails teacherDetails = teacherRepository.findTeacherDetailsByUucmsId(assignTeacherDTO.getTeacherId());
        if (teacherDetails == null) {
            throw new IllegalArgumentException("Teacher not found: " + assignTeacherDTO.getTeacherId());
        }

        // Create and save AssignedTeacher
        AssignedTeacher assignedTeacher = new AssignedTeacher();
        assignedTeacher.setSubject(subject);
        assignedTeacher.setClassEntity(classEntity);
        assignedTeacher.setTeacher(teacherDetails);

        assignedTeacherRepository.save(assignedTeacher);
    }

    public List<AssignedTeacher> getMyClasses(String uucmsId){
        TeacherDetails teacherDetails = teacherRepository.findTeacherDetailsByUucmsId(uucmsId);
        List<AssignedTeacher> assignedTeacher = assignedTeacherRepository.findAssignedTeachersByTeacherId(teacherDetails.getId());
        if (assignedTeacher == null){
            throw new IllegalArgumentException("teacher doesn't have any class assigned");
        }else {
            return assignedTeacher;
        }
    }

    public String getAssignedTeacherBySubjectIdAndClassId(int subjectId,int classId) {
        return assignedTeacherRepository.getAssignedTeacherFullNameBySubjectIdAndClassId(subjectId,classId);
    }

    public void updateTeacher(AssignTeacherDTO assignTeacherDTO) {
        assignedTeacherRepository.updateTeacher(assignTeacherDTO.getTeacherId(),assignTeacherDTO.getClassId(),assignTeacherDTO.getSubjectId());
    }

    public void deleteAll() {
        assignedTeacherRepository.deleteAll();
    }
}
