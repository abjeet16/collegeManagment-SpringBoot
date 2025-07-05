package com.CMS.dto.responses;

import com.CMS.models.ClassEntity;
import com.CMS.models.Subject;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ClassWithSubjectDto {
    private final ClassEntity classEntity;
    private final Subject subject;
}
