package com.mynotes.repository;

import com.mynotes.models.AssignedTeacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignedTeacherRepository extends JpaRepository<AssignedTeacher, Integer> {
}
