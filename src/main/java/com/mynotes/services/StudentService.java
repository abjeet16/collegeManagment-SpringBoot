package com.mynotes.services;

import com.mynotes.dto.responses.AllStudentsOfAClass;
import com.mynotes.dto.responses.StudentDetailsResponse;
import com.mynotes.models.Attendance;
import com.mynotes.models.StudentDetails;
import com.mynotes.repository.ClassRepository;
import com.mynotes.repository.StudentDetailsRepository;
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
    public String deleteStudent(int classId) {
        try {
            studentRepository.deleteByClassEntityId(classId);  // assumes method is correctly defined
            classRepository.deleteById(classId);
            attendanceService.deleteAttendanceByClassId(classId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete class and students: " + e.getMessage(), e);
        }
        return "deleted successfully.";
    }
}
