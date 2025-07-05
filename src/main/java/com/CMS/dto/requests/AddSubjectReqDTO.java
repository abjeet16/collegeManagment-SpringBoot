package com.CMS.dto.requests;

import lombok.Data;

@Data
public class AddSubjectReqDTO {

    private String subjectId;

    private String subjectName;

    private String course;

    private int semester;
}
