package com.mynotes.services.viewServices;

import com.mynotes.models.views.StudentAttendanceSummary;
import com.mynotes.repository.viewRepo.StudentAttendanceSummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentAttendanceSummaryService {

    @Autowired
    private StudentAttendanceSummaryRepository repository;

    @Transactional
    public List<StudentAttendanceSummary> getAttendanceSummary(Long classId, Long subjectId, String studentId) {
        return repository.findByClassIdAndSubjectIdAndStudentId(classId, subjectId, studentId);
    }
}

