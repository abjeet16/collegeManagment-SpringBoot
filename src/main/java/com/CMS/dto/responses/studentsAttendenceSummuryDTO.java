package com.CMS.dto.responses;

import lombok.Data;

@Data
public class studentsAttendenceSummuryDTO {
    private String studentName;
    private String studentId;
    private Double percentage;
}
