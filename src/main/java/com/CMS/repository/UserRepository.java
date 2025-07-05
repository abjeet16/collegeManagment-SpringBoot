package com.CMS.repository;

import com.CMS.dto.responses.AllStudentsOfAClass;
import com.CMS.dto.responses.UserProfileDTO;
import com.CMS.models.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRepository extends CrudRepository<User, String> {

    @Query(value = "SELECT email FROM users WHERE email = :email", nativeQuery = true)
    List<String> doesEmailExist(@Param("email")String email);

    @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
    User getUserByEmail(@Param("email")String email);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO users (Uucms_id,first_name, last_name, email, password, role,phone) VALUES (:User_name,:first_name, :last_name, :email, :password, :role,:phone)", nativeQuery = true)
    int signUpUser(@Param("User_name") String User_name,
                   @Param("first_name") String first_name,
                   @Param("last_name") String last_name,
                   @Param("email") String email,
                   @Param("password") String password,
                   @Param("role") String role,
                   @Param("phone") String phone);


    // CHECK IF EMAIL EXISTS:The query is derived from the method name (existsBy + Email).
    boolean existsByEmail(String email);

    //checks if the user exists by email and password
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email AND u.password = :password")
    boolean existsByEmailAndPassword(String email, String password);

    // Query to get password by email
    @Query(value = "SELECT password FROM users WHERE email = :email", nativeQuery = true)
    String getPasswordByEmail(@Param("email") String email);

    @Query(value = "SELECT * FROM users WHERE uucms_id = :uucmsId", nativeQuery = true)
    User getUserByUucmsId(@Param("uucmsId") String uucmsId);

    @Query("SELECT u.email FROM User u")
    Set<String> findAllEmails();

    boolean existsByPhone(Long phone);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.Uucms_id = :id")
    boolean existsByUucmsId(@Param("id") String id);

    @Query("SELECT new com.mynotes.dto.responses.AllStudentsOfAClass(u.first_name || ' ' || u.last_name, u.Uucms_id) FROM User u WHERE u.role = 'ADMIN'")
    List<AllStudentsOfAClass> getAdmins();

    @Query("SELECT new com.mynotes.dto.responses.UserProfileDTO(u.Uucms_id, u.first_name, u.last_name, u.email, u.phone) FROM User u WHERE u.Uucms_id = :adminId")
    UserProfileDTO getUserById(String adminId);
}
// END OF USER REPOSITORY INTERFACE.
