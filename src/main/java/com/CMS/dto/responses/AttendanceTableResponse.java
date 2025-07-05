package com.CMS.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceTableResponse {
    private String studentId;
    private Map<String, Boolean> attendanceMap;
}
