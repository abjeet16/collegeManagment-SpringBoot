package com.mynotes.dto.responses;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class SubjectAndDateDTO {
    private final long schedulePeriod;
    private final LocalDate date;
}
