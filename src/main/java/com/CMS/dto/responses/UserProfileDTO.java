package com.CMS.dto.responses;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserProfileDTO {
    private final String universityId;
    private final String first_name;
    private final String last_name;
    private final String email;
    private final Long phone;
}
