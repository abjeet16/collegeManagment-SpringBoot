package com.mynotes.repository;

import com.mynotes.dto.responses.AllTeachersDTO;
import com.mynotes.dto.responses.TeacherDetailResponse;
import com.mynotes.enums.Role;
import com.mynotes.models.TeacherDetails;
import com.mynotes.models.User;
import org.apache.xmlbeans.impl.xb.xsdschema.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeacherDetailsRepository extends JpaRepository<TeacherDetails,Integer> {
    // Custom query to fetch teacher details by UUCMS ID
    @Query("SELECT td FROM TeacherDetails td JOIN FETCH td.user u WHERE u.Uucms_id = :uucmsId")
    TeacherDetails findTeacherDetailsByUucmsId(String uucmsId);

    // Fetch all teachers as DTOs
    @Query("SELECT new com.mynotes.dto.responses.AllTeachersDTO( "
            + "CONCAT(t.user.first_name, ' ', t.user.last_name), "  // teacherName
            + "t.user.Uucms_id,"                                   // teacherId
            + "t.department) "                                      // department
            + "FROM TeacherDetails t")
    List<AllTeachersDTO> findAllTeachers();

    @Query("SELECT new com.mynotes.dto.responses.TeacherDetailResponse(t.department, u.phone, u.email, u.last_name, u.first_name, u.Uucms_id) " +
            "FROM TeacherDetails t JOIN t.user u WHERE u.Uucms_id = :uucmsId")
    TeacherDetailResponse getTeacherDetailsByUucmsId(@Param("uucmsId") String uucmsId);
}
