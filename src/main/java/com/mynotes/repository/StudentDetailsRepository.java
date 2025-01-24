package com.mynotes.repository;

import com.mynotes.models.StudentDetails;
import com.mynotes.models.TeacherDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentDetailsRepository extends JpaRepository<StudentDetails,Integer> {
    @Query("SELECT sd FROM StudentDetails sd JOIN FETCH sd.user u WHERE u.Uucms_id = :uucmsId")
    StudentDetails findStudentDetailsByUucmsId(String uucmsId);

    List<StudentDetails> findByClassEntityId(int classId);
}
