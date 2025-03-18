package com.mynotes.dto.responses;

import com.mynotes.enums.Department;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AllTeachersDTO {
    private String teacherName;
    private String teacherId;
    private Department department;

    public AllTeachersDTO(String teacherName, String teacherId, Department department) {
        this.teacherName = teacherName;
        this.teacherId = teacherId;
        this.department = department;
    }
}

