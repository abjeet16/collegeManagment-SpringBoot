package com.CMS.services;

import com.CMS.dto.responses.AllStudentsOfAClass;
import com.CMS.dto.responses.StudentDetailsResponse;
import com.CMS.repository.ClassRepository;
import com.CMS.repository.StudentDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentDetailsRepository studentRepository;
    private final ClassRepository classRepository;
    private final AttendanceService attendanceService;


    public List<AllStudentsOfAClass> getStudentsOfAClass(int classId) {
        List<AllStudentsOfAClass> studentDetails = studentRepository.findByClassEntityIdo(classId);
        if (studentDetails.isEmpty()) {
            throw new IllegalArgumentException("No students found for class ID: " + classId);
        }
        return studentDetails;
    }

    public StudentDetailsResponse getStudentById(String studentId) {
        return studentRepository.getStudentDetailsByUserId(studentId);
    }

    @Transactional
    public String deleteStudents(int classId) {
        try {
            studentRepository.deleteByClassEntityId(classId);
            classRepository.deleteById(classId);
            attendanceService.deleteAttendanceByClassId(classId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete class and students: " + e.getMessage(), e);
        }
        return "deleted successfully.";
    }
}
