package com.mynotes.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceUpdateRequest {
    private String studentId;
    private String subjectId;
    private LocalDate date;
    private boolean status;
}

