package com.CMS.services;

import com.CMS.dto.requests.AttendanceSheetRequest;
import com.CMS.dto.responses.*;
import com.CMS.enums.AttendanceStatus;
import com.CMS.models.Attendance;
import com.CMS.models.views.StudentAttendanceSummary;
import com.CMS.repository.AttendanceRepository;
import com.CMS.repository.StudentDetailsRepository;
import com.CMS.repository.SubjectRepository;
import com.CMS.repository.viewRepo.StudentAttendanceSummaryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for handling attendance-related operations.
 * It interacts with repositories and manages transactions.
 */
@Service
@RequiredArgsConstructor  // Generates a constructor for final fields automatically
public class AttendanceService {

    // Injects the AttendanceRepository to interact with the Attendance table
    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    StudentDetailsRepository studentRepository;

    @Autowired
    private StudentAttendanceSummaryRepository studentAttendanceSummaryRepository;

    // EntityManager is used for managing database transactions
    @PersistenceContext
    private EntityManager entityManager;

    // Inject repositories for Subject and StudentDetails
    private final SubjectRepository subjectRepository;

    @Transactional  // Ensures atomicity (all operations succeed or fail together)
    public AttendanceResponseDTO getAllMyAttendance(String studentId) {
        int semester = studentRepository.getSemesterByUserId(studentId);
        List<StudentAttendanceSummary> summaryList = studentAttendanceSummaryRepository.findByIdStudentIdAndSemester(studentId, semester);

        // Return an empty response instead of null
        if (summaryList.isEmpty()) {
            return new AttendanceResponseDTO();
        }

        AttendanceResponseDTO response = new AttendanceResponseDTO();
        int totalClasses = 0;
        int totalPresent = 0;

        for (StudentAttendanceSummary summary : summaryList) {
            // Use Optional to avoid null values
            String subjectName = Optional.ofNullable(subjectRepository.getSubjectNameById(summary.getId().getSubjectId()))
                    .orElse("Unknown Subject");

            SubjectAttendanceDTO subject = new SubjectAttendanceDTO(
                    subjectName,
                    summary.getId().getSubjectId().intValue(),
                    summary.getTotalClasses(),
                    summary.getTotalPresent(),
                    summary.getAttendancePercentage()
            );

            if (response.getSubjectAttendances() == null) {
                response.setSubjectAttendances(new ArrayList<>());
            }
            response.getSubjectAttendances().add(subject);

            totalClasses += summary.getTotalClasses();
            totalPresent += summary.getTotalPresent();
        }

        // Prevent division by zero
        double percentage = (totalClasses == 0) ? 0.0 : (totalPresent * 100.0) / totalClasses;
        response.setPercentageCount(percentage);

        return response;
    }


    @Transactional(readOnly = true) // Optimized for read operations
    public List<SubjectAndDateDTO> getSubjectAbsent(String subjectId, String userId) {
        return attendanceRepository.findAbsentRecords(Long.parseLong(subjectId), userId);
    }


    @Transactional // Ensures all inserts are committed together or rolled back in case of failure
    public void saveAllBatch(List<Attendance> attendanceList) {
        int batchSize = 50; // Batch size for optimized inserts

        // Loop through attendance list and persist records in batches
        for (int i = 0; i < attendanceList.size(); i++) {
            entityManager.persist(attendanceList.get(i));

            // Flush and clear EntityManager every batchSize records to free memory
            if (i > 0 && i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }

        // Final flush to ensure all records are saved
        entityManager.flush();
        entityManager.clear();
    }

    public List<StudentsSubjectAttendance> getStudentAttendance(String studentId, String subjectId) {
        return attendanceRepository.findBySubjectIdAndStudentId(Long.parseLong(subjectId), studentId);
    }

    public String updateAttendance(long id, boolean status) {
        Optional<Attendance> attendanceOptional = attendanceRepository.findById(id);

        if (attendanceOptional.isPresent()) {
            Attendance attendance = attendanceOptional.get();
            attendance.setStatus(status ? AttendanceStatus.PRESENT : AttendanceStatus.ABSENT);
            attendanceRepository.save(attendance);
            return "Attendance updated successfully";
        }

        return "Attendance not found"; // Handle case when attendance record is missing
    }

    public void deleteAttendanceByClassId(int classId) {
        attendanceRepository.deleteByClassId(classId);
    }

    public List<AttendanceTableResponse> getAttendanceTillDate(AttendanceSheetRequest attendanceSheetRequest) {
        List<Attendance> attendance = attendanceRepository.findByClassIdAndSubjectId(attendanceSheetRequest.getClassId(), attendanceSheetRequest.getSubjectId());
        if (attendance.isEmpty()) {
            return new ArrayList<>();
        }
        return buildAttendanceResponse(attendance);
    }
    public boolean existsByClassIdAndSubjectIdAndSchedulePeriodAndAttendanceDate(Long classId, Long subjectId, int schedulePeriod, LocalDate attendanceDate) {
        return attendanceRepository.existsByClassIdAndSubjectIdAndSchedulePeriodAndAttendanceDate(classId, subjectId, schedulePeriod, attendanceDate);
    }

    public static List<AttendanceTableResponse> buildAttendanceResponse(List<Attendance> attendanceList) {
        List<AttendanceTableResponse> responses = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");

        // Convert date + period to formatted string keys
        Set<String> allDatePeriods = attendanceList.stream()
                .map(a -> a.getAttendanceDate().format(formatter) + " (Period " + a.getSchedulePeriod() + ")")
                .collect(Collectors.toSet());

        Map<String, List<Attendance>> attendanceByStudent = attendanceList.stream()
                .collect(Collectors.groupingBy(Attendance::getStudentId));

        for (Map.Entry<String, List<Attendance>> entry : attendanceByStudent.entrySet()) {
            String studentId = entry.getKey();
            Map<String, Boolean> dateMap = new HashMap<>();

            for (String datePeriod : allDatePeriods) {
                dateMap.put(datePeriod, false);
            }

            for (Attendance att : entry.getValue()) {
                String key = att.getAttendanceDate().format(formatter) + " (Period " + att.getSchedulePeriod() + ")";
                dateMap.put(key, att.getStatus() == AttendanceStatus.PRESENT);
            }

            responses.add(new AttendanceTableResponse(studentId, dateMap));
        }

        return responses;
    }

    public void deleteSubjectAttendance(Integer subjectId) {
        attendanceRepository.deleteBySubjectId(subjectId);
    }
}



