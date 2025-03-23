package com.mynotes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BulkStudentRegistrationRequest {
    private String course;
    private String section;
    private String batchYear;
    private List<StudentRegistrationDTO> students;
}
