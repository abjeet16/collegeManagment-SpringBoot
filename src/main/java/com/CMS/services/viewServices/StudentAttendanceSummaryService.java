package com.CMS.services.viewServices;

import com.CMS.dto.responses.studentsAttendenceSummuryDTO;
import com.CMS.models.views.StudentAttendanceSummary;
import com.CMS.repository.viewRepo.StudentAttendanceSummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentAttendanceSummaryService {

    @Autowired
    private StudentAttendanceSummaryRepository repository;

    @Transactional
    public List<studentsAttendenceSummuryDTO> getAttendanceSummary(Long classId, Long subjectId) {
        List<StudentAttendanceSummary> studentAttendanceSummary = repository.findByIdClassIdAndIdSubjectId(classId, subjectId);
        List<studentsAttendenceSummuryDTO> studentsAttendenceSummuryDTOS= new ArrayList<>();
        for (StudentAttendanceSummary studentAttendanceSummary1 : studentAttendanceSummary) {
            studentsAttendenceSummuryDTO studentsAttendenceSummuryDTO = new studentsAttendenceSummuryDTO();
            studentsAttendenceSummuryDTO.setStudentId(studentAttendanceSummary1.getId().getStudentId());
            studentsAttendenceSummuryDTO.setStudentName(studentAttendanceSummary1.getName());
            studentsAttendenceSummuryDTO.setPercentage(studentAttendanceSummary1.getAttendancePercentage());
            studentsAttendenceSummuryDTOS.add(studentsAttendenceSummuryDTO);
        }
        return studentsAttendenceSummuryDTOS;
    }
}

