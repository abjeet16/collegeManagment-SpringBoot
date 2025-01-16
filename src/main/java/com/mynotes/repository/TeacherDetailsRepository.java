package com.mynotes.repository;

import com.mynotes.models.TeacherDetails;
import com.mynotes.models.User;
import org.apache.xmlbeans.impl.xb.xsdschema.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TeacherDetailsRepository extends JpaRepository<TeacherDetails,Integer> {
    // Custom query to fetch teacher details by UUCMS ID
    @Query("SELECT td FROM TeacherDetails td JOIN FETCH td.user u WHERE u.Uucms_id = :uucmsId")
    TeacherDetails findTeacherDetailsByUucmsId(String uucmsId);
}
