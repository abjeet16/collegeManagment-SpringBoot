package com.mynotes.services;

import com.mynotes.dto.responses.AttendanceResponseDTO;
import com.mynotes.dto.responses.SubjectAttendanceDTO;
import com.mynotes.enums.AttendanceStatus;
import com.mynotes.models.Attendance;
import com.mynotes.models.StudentDetails;
import com.mynotes.models.Subject;
import com.mynotes.repository.AttendanceRepository;
import com.mynotes.repository.StudentDetailsRepository;
import com.mynotes.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

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

    public Long getPresentCountByStudentId(String studentId) {
        return attendanceRepository.countPresentByStudentId(studentId);
    }

    public Long getAbsentCountByStudentId(String studentId) {
        return attendanceRepository.countAbsentByStudentId(studentId);
    }

    public Integer getTotalAttendencePercentage(String studentId) {
        Long presentCount = getPresentCountByStudentId(studentId);
        Long totalCount = presentCount + getAbsentCountByStudentId(studentId);
        return Math.toIntExact((presentCount * 100) / totalCount);
    }

    @Transactional
    public AttendanceResponseDTO getAllMyAttendance(String studentId) {
        StudentDetails studentDetails = studentDetailsRepository.findStudentDetailsByUucmsId(studentId);
        if (studentDetails == null){
            throw new IllegalArgumentException("Student not found: " + studentId);
        }

        List<Subject> subjects = subjectRepository.findByCourses(studentDetails.getClassEntity().getCourse());
        AttendanceResponseDTO attendanceResponseDTO = new AttendanceResponseDTO();

        // Set total attendance percentage
        attendanceResponseDTO.setPercentageCount(getTotalAttendencePercentage(studentId));

        // List to store subject-wise attendance details
        List<SubjectAttendanceDTO> subjectAttendanceDTOs = new ArrayList<>();

        for (Subject subject : subjects) {
            SubjectAttendanceDTO subjectAttendanceDTO = new SubjectAttendanceDTO();

            // Set the subject name from the subject object
            subjectAttendanceDTO.setSubjectName(subject.getSubjectName());

            subjectAttendanceDTO.setSubjectId(subject.getId());

            // Calculate the total number of attendance records for this student and subject
            long totalAttendanceCount = attendanceRepository.countByStudentIdAndSubjectIdAndClassId(studentId, (long) subject.getId(), (long) studentDetails.getClassEntity().getId());
            subjectAttendanceDTO.setTotalClasses(totalAttendanceCount);

            // Calculate the number of present days
            long presentCount = attendanceRepository.countByStudentAndSubjectAndClassAndStatus(studentId, (long) subject.getId(), (long) studentDetails.getClassEntity().getId(), AttendanceStatus.PRESENT);
            subjectAttendanceDTO.setAttendedClasses(presentCount);

            // Calculate the attendance percentage
            if (totalAttendanceCount > 0) {
                double attendancePercentage = ((double) presentCount / totalAttendanceCount) * 100;
                subjectAttendanceDTO.setAttendancePercentage(attendancePercentage);
            } else {
                subjectAttendanceDTO.setAttendancePercentage(0);  // In case there are no records
            }

            // Add the subject attendance details to the list
            subjectAttendanceDTOs.add(subjectAttendanceDTO);
        }

        // Set the list of subject attendance details in the response
        attendanceResponseDTO.setSubjectAttendances(subjectAttendanceDTOs);

        return attendanceResponseDTO;
    }

    public List<Attendance> getSubjectAbsent(String subjectId, String userId) {
        return attendanceRepository.findBySubjectIdAndStudentIdAndStatus(Long.parseLong(subjectId), userId, AttendanceStatus.ABSENT);
    }
}

