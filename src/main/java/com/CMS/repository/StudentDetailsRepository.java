package com.CMS.repository;

import com.CMS.dto.responses.AllStudentsOfAClass;
import com.CMS.dto.responses.StudentDetailsResponse;
import com.CMS.models.StudentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface StudentDetailsRepository extends JpaRepository<StudentDetails, Integer> {

    @Query("SELECT new com.mynotes.dto.responses.AllStudentsOfAClass(u.first_name || ' ' || u.last_name, u.Uucms_id) " +
            "FROM StudentDetails sd " +
            "JOIN sd.user u " +
            "WHERE sd.classEntity.id = :classId")
    List<AllStudentsOfAClass> findByClassEntityIdo(@Param("classId") int classId);


    @Query("SELECT new com.mynotes.dto.responses.StudentDetailsResponse(" +
            "u.first_name, u.last_name, u.email, u.phone, c.section, co.courseName) " +
            "FROM StudentDetails sd " +
            "JOIN sd.user u " +
            "JOIN sd.classEntity c " +
            "JOIN c.course co " +
            "WHERE u.Uucms_id = :uucmsId")
    StudentDetailsResponse getStudentDetailsByUserId(@Param("uucmsId") String uucmsId);

    @Modifying
    @Transactional
    @Query("DELETE FROM StudentDetails sd WHERE sd.classEntity.id = :classId")
    void deleteByClassEntityId(@Param("classId") int classId);

    @Query("SELECT sd.classEntity.currentSemester FROM StudentDetails sd WHERE sd.user.Uucms_id = :studentId")
    int getSemesterByUserId(String studentId);
}

