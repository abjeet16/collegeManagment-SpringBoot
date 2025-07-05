package com.CMS.dto.responses;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class AllStudentsOfAClass {
    private final String studentName;
    private final String studentId;
}
