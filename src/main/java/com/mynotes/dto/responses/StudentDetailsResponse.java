package com.mynotes.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentDetailsResponse {
    private String firstName;
    private String lastName;
    private String email;
    private Long phone;
    private String section;
    private String courseName;
}

