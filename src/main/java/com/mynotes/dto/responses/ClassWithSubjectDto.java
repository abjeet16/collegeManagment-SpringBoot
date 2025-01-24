package com.mynotes.dto.responses;

import com.mynotes.models.ClassEntity;
import com.mynotes.models.Subject;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ClassWithSubjectDto {
    private final ClassEntity classEntity;
    private final Subject subject;
}
