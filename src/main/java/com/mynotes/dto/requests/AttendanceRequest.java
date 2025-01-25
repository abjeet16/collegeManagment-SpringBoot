package com.mynotes.dto.requests;

import lombok.Data;

import java.util.List;

@Data
public class AttendanceRequest {
    private List<AddAttendance> attendanceRecords;
}
