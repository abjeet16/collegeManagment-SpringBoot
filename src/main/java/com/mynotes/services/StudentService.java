package com.mynotes.services;

import com.mynotes.dto.responses.AllStudentsOfAClass;
import com.mynotes.models.StudentDetails;
import com.mynotes.repository.StudentDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentDetailsRepository studentRepository;

    public List<AllStudentsOfAClass> getStudentsOfAClass(int classId) {
        List<StudentDetails> studentDetails = studentRepository.findByClassEntityId(classId);
        if (studentDetails.isEmpty()) {
            throw new IllegalArgumentException("No students found for class ID: " + classId);
        }
        return studentDetails.stream().map(student -> new AllStudentsOfAClass(student.getStudentName(),student.getUser().getUucms_id())).toList();
    }
}
