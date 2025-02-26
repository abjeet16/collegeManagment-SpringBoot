package com.mynotes.services;

import com.mynotes.dto.requests.AddClassReqDTO;
import com.mynotes.models.ClassEntity;
import com.mynotes.models.Courses;
import com.mynotes.repository.ClassRepository;
import com.mynotes.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClassService {

    private final ClassRepository classRepository;

    private final CourseRepository courseRepository;

    public void addClass(AddClassReqDTO addClassReqDTO) {
        ClassEntity classEntity = new ClassEntity();
        classEntity.setBatchYear(addClassReqDTO.getBatchYear());
        classEntity.setSection(addClassReqDTO.getSection());

        Courses course = courseRepository.findByCourseName(addClassReqDTO.getCourse().toUpperCase());
        if (course == null) {
            throw new IllegalArgumentException("Course not found: " + addClassReqDTO.getCourse());
        }
        classEntity.setCourse(course);
        classRepository.save(classEntity);
    }
}
