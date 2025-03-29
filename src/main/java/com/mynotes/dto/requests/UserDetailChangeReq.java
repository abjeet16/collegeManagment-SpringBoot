package com.mynotes.dto.requests;

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
    private String Department;
}

