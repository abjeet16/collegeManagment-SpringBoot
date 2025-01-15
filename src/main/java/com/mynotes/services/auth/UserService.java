package com.mynotes.services.auth;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mynotes.models.*;
import com.mynotes.repository.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentDetailsRepository studentDetailsRepository;

    @Autowired
    private TeacherDetailsRepository teacherDetailsRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ClassRepository classRepository;

    public User loadUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    public User loadUserByUucms_id(String uucms_id) {
        return userRepository.getUserByUucmsId(uucms_id);
    }

    public List<String> doesEmailExist(String email) {
        return userRepository.doesEmailExist(email);
    }

    public int signUpUser(String userName, String firstName, String lastName, String email, String password, String role, String phoneNumber) {
        return userRepository.signUpUser(userName, firstName, lastName, email, password, role, phoneNumber);
    }

    @Transactional
    public int addStudentUsers(String userName, String firstName, String lastName, String email, String password, String role, String phoneNumber, StudentDetails studentDetails, String upperCase, String section, int batchYear) {
        int result = userRepository.signUpUser(userName, firstName, lastName, email, password, role, phoneNumber);

        Courses course = courseRepository.findByCourseName(upperCase);
        if (course == null) {
            throw new IllegalArgumentException("Course not found: " + upperCase);
        }
        // Validate course name before proceeding
        ClassEntity classEntity = classRepository.findBySectionAndBatchYearAndCourse(section,batchYear, course);
        if (classEntity == null) {
            throw new IllegalArgumentException("class not found");
        }
        studentDetails.setClassEntity(classEntity);
        studentDetailsRepository.save(studentDetails);
        addStudentToFireBase(studentDetails);
        return result;
    }

    private void addStudentToFireBase(StudentDetails studentDetails) {
        try {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

            // Validate StudentDetails fields
            if (studentDetails == null || studentDetails.getClassEntity().getCourse() == null || studentDetails.getUser() == null) {
                throw new IllegalArgumentException("Invalid student details. Cannot proceed.");
            }

            String courseName = studentDetails.getClassEntity().getCourse().getCourseName();
            String section = studentDetails.getClassEntity().getSection();
            String batchYear = String.valueOf(studentDetails.getClassEntity().getBatchYear());
            User user = studentDetails.getUser();
            String userId = user.getUucms_id();
            String name = user.getFirst_name() + " " + user.getLast_name();

            if (courseName == null || section == null || userId == null || name == null) {
                throw new IllegalArgumentException("Incomplete student details. Cannot proceed.");
            }

            String classKey = courseName + "_" + section + "_" + batchYear;

            DatabaseReference studentRef = databaseReference.child(classKey).child(userId);

            Map<String, Object> studentData = new HashMap<>();
            studentData.put("name", name);
            studentData.put("attendance", new HashMap<>());

            studentRef.setValueAsync(studentData);

            System.out.println("Student data saved successfully under: " + classKey + "/" + userId);
        } catch (Exception e) {
            System.err.println("Error saving student to Firebase: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Transactional
    public int addTeacherUsers(String userName, String firstName, String lastName, String email, String password, String role, String phoneNumber, TeacherDetails teacherDetails) {
        int result = userRepository.signUpUser(userName, firstName, lastName, email, password, role, phoneNumber);

        // Validate teacher details before saving
        if (teacherDetails == null) {
            throw new IllegalArgumentException("Teacher details cannot be null.");
        }

        teacherDetailsRepository.save(teacherDetails);
        return result;
    }

    public boolean doesWithEmailExist(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean doesUserWithEmailPasswordExist(String email, String password) {
        return userRepository.existsByEmailAndPassword(email, password);
    }

    public String getPasswordByEmail(String email) {
        return userRepository.getPasswordByEmail(email);
    }
}
