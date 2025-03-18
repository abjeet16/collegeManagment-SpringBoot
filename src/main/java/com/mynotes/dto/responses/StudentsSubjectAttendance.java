package com.mynotes.dto.responses;

import com.mynotes.enums.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentsSubjectAttendance {
    private long id;
    private LocalDate attendanceDate;
    private boolean isPresent;
    private int schedulePeriod;

    // ✅ Explicit constructor matching the JPQL query
    public StudentsSubjectAttendance(Long id,LocalDate attendanceDate, AttendanceStatus status,int schedulePeriod) {
        this.id = id;
        this.attendanceDate = attendanceDate;
        this.schedulePeriod = schedulePeriod;
        this.isPresent = status == AttendanceStatus.PRESENT;
    }
}

