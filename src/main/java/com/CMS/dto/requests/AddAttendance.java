package com.CMS.dto.requests;

import lombok.Data;

@Data
public class AddAttendance {
    private String studentId;
    private Boolean isPresent;
}

