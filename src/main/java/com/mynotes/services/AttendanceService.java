package com.mynotes.services;

import com.mynotes.dto.responses.AttendanceResponseDTO;
import com.mynotes.dto.responses.SubjectAndDateDTO;
import com.mynotes.dto.responses.SubjectAttendanceDTO;
import com.mynotes.enums.AttendanceStatus;
import com.mynotes.models.Attendance;
import com.mynotes.models.StudentDetails;
import com.mynotes.models.Subject;
import com.mynotes.repository.AttendanceRepository;
import com.mynotes.repository.StudentDetailsRepository;
import com.mynotes.repository.SubjectRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private final SubjectRepository subjectRepository;

    private final StudentDetailsRepository studentDetailsRepository;

    // Save a new attendance record
    public Attendance saveAttendance(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    // Get attendance for a specific student
    public List<Attendance> getAttendanceByStudent(Long studentId) {
        return attendanceRepository.findByStudentId(studentId);
    }

    // Get attendance for a specific class and date
    public List<Attendance> getAttendanceByClassAndDate(Long classId, LocalDate date) {
        return attendanceRepository.findByClassAndDate(classId, date);
    }

    // Get summary for a specific subject and class
    public List<Object[]> getSummary(Long subjectId, Long classId) {
        return attendanceRepository.getSummaryBySubjectAndClass(subjectId, classId);
    }

    public Integer getTotalAttendancePercentage(String studentId) {
        Integer percentage = attendanceRepository.getAttendancePercentageByStudentId(studentId);
        return percentage != null ? percentage : 0;
    }

    @Transactional
    public AttendanceResponseDTO getAllMyAttendance(String studentId) {
        StudentDetails studentDetails = studentDetailsRepository.findStudentDetailsByUucmsId(studentId);
        if (studentDetails == null) {
            throw new IllegalArgumentException("Student not found: " + studentId);
        }

        Long classId = (long) studentDetails.getClassEntity().getId();
        List<Subject> subjects = subjectRepository.findByCourses(studentDetails.getClassEntity().getCourse());

        // Fetch batch attendance data
        List<Object[]> attendanceData = attendanceRepository.findAttendanceSummaryByStudent(studentId, classId);

        // Create a Map for quick lookup
        Map<Long, SubjectAttendanceDTO> attendanceMap = new HashMap<>();

        // Initialize all subjects with default attendance (0)
        for (Subject subject : subjects) {
            SubjectAttendanceDTO dto = new SubjectAttendanceDTO();
            dto.setSubjectId(subject.getId());
            dto.setSubjectName(subject.getSubjectName());
            dto.setTotalClasses(0);
            dto.setAttendedClasses(0);
            dto.setAttendancePercentage(0.0);
            attendanceMap.put((long) subject.getId(), dto);
        }

        // Update subjects that have attendance records
        for (Object[] record : attendanceData) {
            Long subjectId = ((Number) record[0]).longValue();
            long totalClasses = ((Number) record[1]).longValue();
            long attendedClasses = ((Number) record[2]).longValue();

            if (attendanceMap.containsKey(subjectId)) {
                SubjectAttendanceDTO dto = attendanceMap.get(subjectId);
                dto.setTotalClasses((int) totalClasses);
                dto.setAttendedClasses((int) attendedClasses);

                // Avoid division by zero
                if (totalClasses > 0) {
                    double percentage = ((double) attendedClasses / totalClasses) * 100;
                    dto.setAttendancePercentage(percentage);
                }
            }
        }

        // Create the final response
        AttendanceResponseDTO attendanceResponseDTO = new AttendanceResponseDTO();
        attendanceResponseDTO.setPercentageCount(getTotalAttendancePercentage(studentId));
        attendanceResponseDTO.setSubjectAttendances(new ArrayList<>(attendanceMap.values()));

        return attendanceResponseDTO;
    }

    public List<SubjectAndDateDTO> getSubjectAbsent(String subjectId, String userId) {
        return attendanceRepository.findAbsentRecords(Long.parseLong(subjectId), userId);
    }

    @Transactional
    public void saveAllBatch(List<Attendance> attendanceList) {
        int batchSize = 50;  // Adjust batch size for best performance

        for (int i = 0; i < attendanceList.size(); i++) {
            entityManager.persist(attendanceList.get(i));

            if (i > 0 && i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }

        // Final flush to persist remaining data
        entityManager.flush();
        entityManager.clear();
    }
}

