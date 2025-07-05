package com.CMS.dto.responses;

import com.CMS.enums.Department;
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

