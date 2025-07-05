package com.CMS.services.auth;

import com.CMS.dto.requests.UserDetailChangeReq;
import com.CMS.dto.requests.StudentRegistrationDTO;
import com.CMS.dto.requests.addAdminRequest;
import com.CMS.dto.responses.AllStudentsOfAClass;
import com.CMS.dto.responses.AllTeachersDTO;
import com.CMS.dto.responses.TeacherDetailResponse;
import com.CMS.dto.responses.UserProfileDTO;
import com.CMS.enums.Role;
import com.CMS.models.*;
import com.CMS.repository.*;
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

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private AssignedTeacherRepository assignedTeacherRepository;

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

    @Transactional
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
    public String changeUserDetails(UserDetailChangeReq userDetailChangeReq) {
        User user = userRepository.getUserByUucmsId(userDetailChangeReq.getUniversityId());
        if (user == null) {
            return "No User with UUCMS ID: " + userDetailChangeReq.getUniversityId();
        } else {
            String result = checkDetails(userDetailChangeReq,user);
            if (result != null) {
                return result;
            }

            if (userDetailChangeReq.getDepartment() != null) {
                TeacherDetails teacherDetails = teacherDetailsRepository.findTeacherDetailsByUucmsId(userDetailChangeReq.getUniversityId());
                teacherDetails.setDepartment(userDetailChangeReq.getDepartment().toString());
                teacherDetailsRepository.save(teacherDetails);
            }

            user.setFirst_name(userDetailChangeReq.getFirstName());
            user.setLast_name(userDetailChangeReq.getLastName());
            user.setEmail(userDetailChangeReq.getEmail());
            user.setPhone(userDetailChangeReq.getPhone());
            userRepository.save(user);
            return "Details changed successfully";
        }
    }

    private String checkDetails(UserDetailChangeReq userDetailChangeReq, User user) {
        boolean emailUnchanged = userDetailChangeReq.getEmail() == null || userDetailChangeReq.getEmail().equals(user.getEmail());
        boolean phoneUnchanged = userDetailChangeReq.getPhone() == null || userDetailChangeReq.getPhone().equals(user.getPhone());
        boolean firstNameUnchanged = userDetailChangeReq.getFirstName() == null || userDetailChangeReq.getFirstName().equals(user.getFirst_name());
        boolean lastNameUnchanged = userDetailChangeReq.getLastName() == null || userDetailChangeReq.getLastName().equals(user.getLast_name());

        // Check email conflict
        if (userDetailChangeReq.getEmail() != null &&
                userRepository.existsByEmail(userDetailChangeReq.getEmail()) &&
                !userDetailChangeReq.getEmail().equals(user.getEmail())) {
            return "Email already exists";
        }

        // Check phone conflict
        if (userDetailChangeReq.getPhone() != null &&
                userRepository.existsByPhone(userDetailChangeReq.getPhone()) &&
                !userDetailChangeReq.getPhone().equals(user.getPhone())) {
            return "Phone number already exists";
        }

        // Check department difference if applicable
        boolean departmentUnchanged = true;
        if (userDetailChangeReq.getDepartment() != null) {
            TeacherDetails teacherDetails = teacherDetailsRepository.findTeacherDetailsByUucmsId(userDetailChangeReq.getUniversityId());
            if (teacherDetails == null || !userDetailChangeReq.getDepartment().equals(teacherDetails.getDepartment())) {
                departmentUnchanged = false;
            }
        }

        // Now check if everything is unchanged
        if (emailUnchanged && phoneUnchanged && firstNameUnchanged && lastNameUnchanged && departmentUnchanged) {
            return "No changes made";
        }

        return null;
    }

    public List<AllStudentsOfAClass> getAdmins() {
        return userRepository.getAdmins();
    }

    public String addAdmin(addAdminRequest addAdminReqDTO) {
        String result = verifyAdminDetails(addAdminReqDTO);
        if (result != null) {
            return result;
        }
        try {
            long phone = Long.parseLong(addAdminReqDTO.getPhone());
        } catch (NumberFormatException e) {
            return "Invalid phone number";
        }
        User user = new User(addAdminReqDTO.getUserName(), addAdminReqDTO.getFirstName(), addAdminReqDTO.getLastName(), addAdminReqDTO.getEmail(), Long.valueOf(addAdminReqDTO.getPhone()), addAdminReqDTO.getPassword(), Role.ADMIN);
        userRepository.save(user);
        return "Admin added successfully!";
    }

    private String verifyAdminDetails(addAdminRequest addAdminReqDTO) {
        if (userRepository.existsByEmail(addAdminReqDTO.getEmail())) {
            return "Email already exists";
        } else if (userRepository.existsByPhone(Long.valueOf(addAdminReqDTO.getPhone()))) {
            return "Phone number already exists";
        } else if (userRepository.existsByUucmsId(addAdminReqDTO.getUserName())) {
            return "Username already exists";
        }
        return null;
    }

    public UserProfileDTO getAdminById(String adminId) {
        return userRepository.getUserById(adminId);
    }

    @Transactional
    public String deleteTeacher(String teacherId) {
        try {
            userRepository.deleteById(teacherId);
            return "Teacher deleted successfully";
        } catch (Exception e) {
            return "Error deleting teacher: " + e.getMessage();
        }
    }

    public String deleteStudent(String studentId) {
        try {
            attendanceRepository.deleteByStudentId(studentId);
            //studentDetailsRepository.deleteByUucmsId(studentId);
            userRepository.deleteById(studentId);
            return "Student deleted successfully";
        }catch (Exception e){
            return "Error deleting student: " + e.getMessage();
        }
    }
}

