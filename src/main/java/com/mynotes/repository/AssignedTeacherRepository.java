package com.mynotes.repository;

import com.mynotes.models.AssignedTeacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssignedTeacherRepository extends JpaRepository<AssignedTeacher, Integer> {
    @Query("SELECT at FROM AssignedTeacher at JOIN FETCH at.teacher t JOIN FETCH at.classEntity c JOIN FETCH at.subject s WHERE t.id = :teacherId")
    List<AssignedTeacher> findAssignedTeachersByTeacherId(@Param("teacherId") int teacherId);
}
