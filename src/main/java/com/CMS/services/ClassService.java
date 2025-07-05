package com.CMS.services;

import com.CMS.dto.requests.AddClassReqDTO;
import com.CMS.models.ClassEntity;
import com.CMS.models.Courses;
import com.CMS.repository.ClassRepository;
import com.CMS.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassService {

    private final ClassRepository classRepository;

    private final CourseRepository courseRepository;

    private final AssignedTeacherService assignedTeacherService;

    public void addClass(AddClassReqDTO addClassReqDTO) {
        ClassEntity classEntity = new ClassEntity();
        classEntity.setBatchYear(addClassReqDTO.getBatchYear());
        classEntity.setSection(addClassReqDTO.getSection());
        classEntity.setCurrentSemester(addClassReqDTO.getCurrentSemester());

        Courses course = courseRepository.findByCourseName(addClassReqDTO.getCourse().toUpperCase());
        if (course == null) {
            throw new IllegalArgumentException("Course not found: " + addClassReqDTO.getCourse());
        }
        if (classRepository.existsBySectionAndCourseAndCurrentSemester(addClassReqDTO.getSection(),course,addClassReqDTO.getCurrentSemester())){
            throw new IllegalArgumentException("Class already exists: " + addClassReqDTO.getSection() + " - " + addClassReqDTO.getBatchYear());
        }
        classEntity.setCourse(course);
        classRepository.save(classEntity);
    }

    public List<ClassEntity> getClassesByCourseId(int courseId) {
        return classRepository.getClassesByCourseId(courseId);
    }

    public int getCurrentSemester(int classId) {
        return classRepository.getCurrentSemester(classId);
    }

    @Transactional
    public String promoteStudents() {
        classRepository.promoteAllClasses();
        assignedTeacherService.deleteAll();
        return "Promoted successfully";
    }

    @Transactional
    public String demoteStudents() {
        classRepository.demoteAllClasses();
        assignedTeacherService.deleteAll();
        return "Demoted successfully";
    }

    public void updateClassDetails(int classId, AddClassReqDTO addClassReqDTO) {
        ClassEntity classEntity = classRepository.findById(classId).orElse(null);
        if (classEntity == null) {
            throw new IllegalArgumentException("Class not found: " + classId);
        }
        Courses course = courseRepository.findByCourseName(addClassReqDTO.getCourse().toUpperCase());
        if (course == null) {
            throw new IllegalArgumentException("Course not found: " + addClassReqDTO.getCourse());
        }
        if(classRepository.existsBySectionAndCourseAndCurrentSemester(addClassReqDTO.getSection(), course, addClassReqDTO.getCurrentSemester())) {
            throw new IllegalArgumentException("Class already exists: " + addClassReqDTO.getSection() + " - " + addClassReqDTO.getBatchYear());
        }
        classEntity.setBatchYear(addClassReqDTO.getBatchYear());
        classEntity.setSection(addClassReqDTO.getSection());
        classEntity.setCurrentSemester(addClassReqDTO.getCurrentSemester());
        classRepository.save(classEntity);
    }
}
