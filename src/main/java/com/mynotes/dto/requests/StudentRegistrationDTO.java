package com.mynotes.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentRegistrationDTO {
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String course;
    private String section;
    private int batchYear;
    private String phone;
}
