package com.mynotes.repository.viewRepo;

import com.mynotes.models.views.AttendanceSummaryId;
import com.mynotes.models.views.StudentAttendanceSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentAttendanceSummaryRepository extends JpaRepository<StudentAttendanceSummary, AttendanceSummaryId> {

    List<StudentAttendanceSummary> findByIdClassIdAndIdSubjectId(Long classId, Long subjectId);

    List<StudentAttendanceSummary> findByIdStudentId(String studentId);
}

