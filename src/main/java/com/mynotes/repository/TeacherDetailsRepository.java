package com.mynotes.repository;

import com.mynotes.models.TeacherDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherDetailsRepository extends JpaRepository<TeacherDetails,Integer> {
}
