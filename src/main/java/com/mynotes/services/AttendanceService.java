package com.mynotes.services;

import com.mynotes.dto.responses.AttendanceResponseDTO;
import com.mynotes.dto.responses.StudentsSubjectAttendance;
import com.mynotes.dto.responses.SubjectAndDateDTO;
import com.mynotes.dto.responses.SubjectAttendanceDTO;
import com.mynotes.enums.AttendanceStatus;
import com.mynotes.models.Attendance;
import com.mynotes.models.StudentDetails;
import com.mynotes.models.Subject;
import com.mynotes.models.views.StudentAttendanceSummary;
import com.mynotes.repository.AttendanceRepository;
import com.mynotes.repository.StudentDetailsRepository;
import com.mynotes.repository.SubjectRepository;
import com.mynotes.repository.viewRepo.StudentAttendanceSummaryRepository;
import com.mynotes.services.viewServices.StudentAttendanceSummaryService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

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
    private StudentAttendanceSummaryRepository studentAttendanceSummaryRepository;

    // EntityManager is used for managing database transactions
    @PersistenceContext
    private EntityManager entityManager;

    // Inject repositories for Subject and StudentDetails
    private final SubjectRepository subjectRepository;
    private final StudentDetailsRepository studentDetailsRepository;

    /**
     * Retrieves the total attendance percentage of a student.
     *
     * @param studentId - The unique ID of the student.
     * @return The attendance percentage (defaults to 0 if no record is found).
     */
    @Transactional(readOnly = true) // Read-only transaction for better performance
    public Integer getTotalAttendancePercentage(String studentId) {
        Integer percentage = attendanceRepository.getAttendancePercentageByStudentId(studentId);
        return percentage != null ? percentage : 0; // Return 0 if percentage is null
    }

    @Transactional  // Ensures atomicity (all operations succeed or fail together)
    public AttendanceResponseDTO getAllMyAttendance(String studentId) {
        List<StudentAttendanceSummary> summaryList = studentAttendanceSummaryRepository.findByIdStudentId(studentId);

        // Return an empty response instead of null
        if (summaryList.isEmpty()) {
            return new AttendanceResponseDTO();
        }

        for (StudentAttendanceSummary summary : summaryList) {
            System.out.println(studentId);
            System.out.println(summary);
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

    /**
     * Retrieves the list of absent dates for a student in a particular subject.
     *
     * @param subjectId - The ID of the subject.
     * @param userId - The ID of the student.
     * @return A list of SubjectAndDateDTO representing absent dates.
     */
    @Transactional(readOnly = true) // Optimized for read operations
    public List<SubjectAndDateDTO> getSubjectAbsent(String subjectId, String userId) {
        return attendanceRepository.findAbsentRecords(Long.parseLong(subjectId), userId);
    }

    /**
     * Saves a batch of attendance records efficiently using bulk insert.
     * Uses EntityManager to avoid performance overhead caused by multiple insert calls.
     *
     * @param attendanceList - List of Attendance records to be saved.
     */
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

    public List<StudentsSubjectAttendance> getStudentAttendence(String studentId, String subjectId) {
        return attendanceRepository.findBySubjectIdAndStudentId(Long.parseLong(subjectId), studentId);
    }

    public String updateAttendance(String studentId, String subjectId, LocalDate date, boolean status) {
        Attendance attendance = attendanceRepository.findBySubjectIdAndStudentIdAndAttendanceDate(Long.parseLong(subjectId), studentId, date);
        if (attendance == null) {
            throw new IllegalArgumentException("Attendance record not found for student: " + studentId + ", subject: " + subjectId + ", date: " + date);
        }
        attendance.setStatus(status ? AttendanceStatus.PRESENT : AttendanceStatus.ABSENT);
        attendanceRepository.save(attendance);
        return "Attendance updated successfully";
    }
}

