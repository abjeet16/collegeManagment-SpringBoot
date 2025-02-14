package com.mynotes.repository;

import com.mynotes.dto.responses.AllStudentsOfAClass;
import com.mynotes.models.StudentDetails;
import com.mynotes.models.TeacherDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentDetailsRepository extends JpaRepository<StudentDetails, Integer> {
    @Query("SELECT sd FROM StudentDetails sd JOIN FETCH sd.user u WHERE u.Uucms_id = :uucmsId")
    StudentDetails findStudentDetailsByUucmsId(String uucmsId);

    @Query("SELECT new com.mynotes.dto.responses.AllStudentsOfAClass(u.first_name || ' ' || u.last_name, u.Uucms_id) " +
            "FROM StudentDetails sd " +
            "JOIN sd.user u " +
            "WHERE sd.classEntity.id = :classId")
    List<AllStudentsOfAClass> findByClassEntityIdo(@Param("classId") int classId);
}

