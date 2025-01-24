package com.mynotes.dto.responses;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Data
@RequiredArgsConstructor
public class AllStudentsOfAClass {
    private final String studentName;
    private final String studentId;
}
