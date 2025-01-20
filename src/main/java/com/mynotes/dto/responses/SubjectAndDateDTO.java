package com.mynotes.dto.responses;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class SubjectAndDateDTO {
    private final long subjectId;
    private final LocalDate date;
}
