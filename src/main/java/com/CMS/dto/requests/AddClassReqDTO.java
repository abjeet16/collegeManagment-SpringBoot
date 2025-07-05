package com.CMS.dto.requests;

import lombok.Data;

@Data
public class AddClassReqDTO {

    private String course;

    private int batchYear;

    private String section;

    private int currentSemester;
}
