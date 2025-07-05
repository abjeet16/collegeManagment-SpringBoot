package com.CMS.dto.requests;

import com.CMS.enums.Department;
import lombok.Data;
import jakarta.annotation.Nullable;

@Data
public class UserDetailChangeReq {

    private String UniversityId;

    @Nullable
    private String firstName;

    @Nullable
    private String lastName;

    @Nullable
    private Long phone;

    @Nullable
    private String email;

    @Nullable
    private String password;

    private String adminPassword;

    @Nullable
    private String department;

    @Nullable
    public Department getDepartment() {
        if (department != null) {
            return Department.valueOf(department.toUpperCase());
        }
        return null;
    }
}

