package com.mynotes.services.auth;

import com.mynotes.dto.requests.UserDetailChangeReq;
import com.mynotes.dto.requests.StudentRegistrationDTO;
import com.mynotes.dto.responses.AllTeachersDTO;
import com.mynotes.dto.responses.TeacherDetailResponse;
import com.mynotes.enums.Role;
import com.mynotes.models.*;
import com.mynotes.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // EntityManager is used for managing database transactions
    @PersistenceContext
    private EntityManager entityManager;

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

        // Remove Firebase logic

        return result;
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

    public List<AllTeachersDTO> getAllTeachers() {
        return teacherDetailsRepository.findAllTeachers();
    }

    public TeacherDetailResponse getTeacherById(String teacherId) {
        return teacherDetailsRepository.getTeacherDetailsByUucmsId(teacherId);
    }

    @Transactional
    public Map<String, Object> saveAllStudentsInBatch(
            List<StudentRegistrationDTO> dtos,
            ClassEntity classEntity
    ) {
        int batchSize = 50;
        int successCount = 0;
        List<String> failedEntries = new ArrayList<>();

        Set<String> existingEmails = userRepository.findAllEmails();

        for (int i = 0; i < dtos.size(); i++) {
            StudentRegistrationDTO dto = dtos.get(i);

            try {
                if (!isRowValid(dto)) {
                    failedEntries.add("Invalid data at index: " + i);
                    continue;
                }

                if (existingEmails.contains(dto.getEmail())) {
                    failedEntries.add("Email already exists: " + dto.getEmail());
                    continue;
                }

                User user = new User(
                        dto.getUserName(),
                        dto.getFirstName(),
                        dto.getLastName(),
                        dto.getEmail(),
                        Long.parseLong(dto.getPhone()),
                        dto.getPassword(),
                        Role.STUDENT
                );

                entityManager.persist(user);

                StudentDetails details = new StudentDetails();
                details.setUser(user);
                details.setClassEntity(classEntity); // 🔁 use reference here

                entityManager.persist(details);

                existingEmails.add(dto.getEmail());
                successCount++;

                if (i > 0 && i % batchSize == 0) {
                    entityManager.flush();
                    entityManager.clear();
                }

            } catch (Exception e) {
                failedEntries.add("Error at index " + i + ": " + e.getMessage());
            }
        }

        entityManager.flush();
        entityManager.clear();

        return Map.of(
                "successCount", successCount,
                "failedEntries", failedEntries
        );
    }

    private boolean isRowValid(StudentRegistrationDTO dto) {
        return dto.getUserName() != null &&
                dto.getFirstName() != null &&
                dto.getLastName() != null &&
                dto.getEmail() != null && dto.getEmail().contains("@") &&
                dto.getPassword() != null &&
                dto.getPhone() != null && !dto.getPhone().isEmpty();
    }

    public String changeUserPassword(UserDetailChangeReq studentDetails) {
        User user = userRepository.getUserByUucmsId(studentDetails.getUniversityId());
        if (user == null) {
            return "No User with UUCMS ID: " + studentDetails.getUniversityId();
        } else {
            user.setPassword(studentDetails.getPassword());
            userRepository.save(user);
            return "Password changed successfully";
        }
    }

    @Transactional
    public String changeUserDetails(UserDetailChangeReq studentDetails) {
        User user = userRepository.getUserByUucmsId(studentDetails.getUniversityId());
        if (user == null) {
            return "No User with UUCMS ID: " + studentDetails.getUniversityId();
        } else {
            String result = checkDetails(studentDetails,user);
            if (result != null) {
                return result;
            }
            user.setFirst_name(studentDetails.getFirstName());
            user.setLast_name(studentDetails.getLastName());
            user.setEmail(studentDetails.getEmail());
            user.setPhone(studentDetails.getPhone());
            if (studentDetails.getDepartment() != null) {
                TeacherDetails teacherDetails = teacherDetailsRepository.findTeacherDetailsByUucmsId(studentDetails.getUniversityId());
                teacherDetails.setDepartment(studentDetails.getDepartment());
                teacherDetailsRepository.save(teacherDetails);
            }
            userRepository.save(user);
            return "Details changed successfully";
        }
    }

    private String checkDetails(UserDetailChangeReq studentDetails, User user) {
        if (userRepository.existsByEmail(studentDetails.getEmail()) && !user.getEmail().equals(studentDetails.getEmail())) {
            return "Email already exists";
        } else if (userRepository.existsByPhone(studentDetails.getPhone()) && !user.getPhone().equals(studentDetails.getPhone())) {
            return "Phone number already exists";
        } else if (studentDetails.getEmail().equals(user.getEmail()) && studentDetails.getPhone().equals(user.getPhone()) && studentDetails.getFirstName().equals(user.getFirst_name()) && studentDetails.getLastName().equals(user.getLast_name())) {
            return "No changes made";
        }
        return null;
    }
}

