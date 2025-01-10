package com.mynotes.dto.requests;

import lombok.Data;

@Data
public class LoginRequest {
    private String uucms_id;
    private String password;
}
// END OF LOGIN REQUEST CLASS.