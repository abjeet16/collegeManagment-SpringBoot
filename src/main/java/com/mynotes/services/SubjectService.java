package com.mynotes.services;

import com.mynotes.dto.requests.AddSubjectReqDTO;
import com.mynotes.models.Courses;
import com.mynotes.models.Subject;
import com.mynotes.repository.CourseRepository;
import com.mynotes.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectsRepository;

    private final CourseRepository courseRepository;

    public void addSubject(AddSubjectReqDTO addSubjectReqDTO) {
        Subject subjects = new Subject();
        subjects.setSubjectId(addSubjectReqDTO.getSubjectId());
        subjects.setSubjectName(addSubjectReqDTO.getSubjectName());
        Courses courses = courseRepository.findByCourseName(addSubjectReqDTO.getCourse().toUpperCase());
        subjects.setCourses(courses);
        subjectsRepository.save(subjects);
    }
}
