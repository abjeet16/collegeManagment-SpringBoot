package com.mynotes.services;

import com.mynotes.models.Attendance;
import com.mynotes.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

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
}

