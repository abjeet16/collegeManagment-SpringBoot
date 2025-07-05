package com.CMS.dto.responses;

import com.CMS.enums.Department;
import lombok.Data;

@Data
public class TeacherDetailResponse {
    private String teacherId;
    private String firstName;
    private String lastName;
    private String email;
    private Long phone;
    private String Department;

    public TeacherDetailResponse(Department department, Long phone, String email, String lastName, String firstName, String teacherId) {
        this.Department = department.toString();
        this.phone = phone;
        this.email = email;
        this.lastName = lastName;
        this.firstName = firstName;
        this.teacherId = teacherId;
    }
}
