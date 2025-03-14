package com.mynotes.repository.viewRepo;

import com.mynotes.models.views.StudentAttendanceSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentAttendanceSummaryRepository extends JpaRepository<StudentAttendanceSummary, String> {

    List<StudentAttendanceSummary> findByClassIdAndSubjectId(Long classId, Long subjectId);
}

