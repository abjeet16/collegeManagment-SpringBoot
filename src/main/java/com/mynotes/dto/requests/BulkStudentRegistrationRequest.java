package com.mynotes.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkStudentRegistrationRequest {
    private Long classEntityId;
    private List<StudentRegistrationDTO> students;
}
