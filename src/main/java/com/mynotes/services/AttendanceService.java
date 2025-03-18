package com.mynotes.services;

import com.mynotes.dto.responses.AttendanceResponseDTO;
import com.mynotes.dto.responses.StudentsSubjectAttendance;
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

    /**
     * Retrieves all attendance records for a specific student.
     *
     * @param studentId - The unique ID of the student.
     * @return AttendanceResponseDTO containing attendance details.
     */
    @Transactional  // Ensures atomicity (all operations succeed or fail together)
    public AttendanceResponseDTO getAllMyAttendance(String studentId) {
        // Fetch student details based on studentId
        StudentDetails studentDetails = studentDetailsRepository.findStudentDetailsByUucmsId(studentId);
        if (studentDetails == null) {
            throw new IllegalArgumentException("Student not found: " + studentId);
        }

        // Get class ID associated with the student
        Long classId = (long) studentDetails.getClassEntity().getId();

        // Retrieve all subjects for the student's course
        List<Subject> subjects = subjectRepository.findByCourses(studentDetails.getClassEntity().getCourse());

        // Fetch attendance summary for the student
        List<Object[]> attendanceData = attendanceRepository.findAttendanceSummaryByStudent(studentId, classId);

        // Map to store attendance details per subject
        Map<Long, SubjectAttendanceDTO> attendanceMap = new HashMap<>();

        // Initialize attendance details with zero values for each subject
        for (Subject subject : subjects) {
            SubjectAttendanceDTO dto = new SubjectAttendanceDTO();
            dto.setSubjectId(subject.getId());
            dto.setSubjectName(subject.getSubjectName());
            dto.setTotalClasses(0);
            dto.setAttendedClasses(0);
            dto.setAttendancePercentage(0.0);
            attendanceMap.put((long) subject.getId(), dto);
        }

        // Update attendance records for each subject based on retrieved data
        for (Object[] record : attendanceData) {
            Long subjectId = ((Number) record[0]).longValue();
            long totalClasses = ((Number) record[1]).longValue();
            long attendedClasses = ((Number) record[2]).longValue();

            if (attendanceMap.containsKey(subjectId)) {
                SubjectAttendanceDTO dto = attendanceMap.get(subjectId);
                dto.setTotalClasses((int) totalClasses);
                dto.setAttendedClasses((int) attendedClasses);

                // Calculate percentage (avoid division by zero)
                if (totalClasses > 0) {
                    double percentage = ((double) attendedClasses / totalClasses) * 100;
                    dto.setAttendancePercentage(percentage);
                }
            }
        }

        // Prepare response object with overall percentage and subject-wise attendance
        AttendanceResponseDTO attendanceResponseDTO = new AttendanceResponseDTO();
        attendanceResponseDTO.setPercentageCount(getTotalAttendancePercentage(studentId));
        attendanceResponseDTO.setSubjectAttendances(new ArrayList<>(attendanceMap.values()));

        return attendanceResponseDTO;
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

