package com.mynotes.repository;

import com.mynotes.models.StudentDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentDetailsRepository extends JpaRepository<StudentDetails,Integer> {

}
