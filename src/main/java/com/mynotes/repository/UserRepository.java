package com.mynotes.repository;

import com.mynotes.models.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    @Query(value = "SELECT email FROM users WHERE email = :email", nativeQuery = true)
    List<String> doesEmailExist(@Param("email")String email);

    @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
    User getUserByEmail(@Param("email")String email);

    // GET USER ID BY EMAIL:
    @Query(value = "SELECT user_id FROM users WHERE email = :email ", nativeQuery = true)
    int getUserIdByEmail(@Param("email") String email);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO users (Uucms_id,first_name, last_name, email, password, role) VALUES (:User_name,:first_name, :last_name, :email, :password, :role)", nativeQuery = true)
    int signUpUser(@Param("User_name") String User_name,
                   @Param("first_name") String first_name,
                   @Param("last_name") String last_name,
                   @Param("email") String email,
                   @Param("password") String password,
                   @Param("role") String role);


    // CHECK IF EMAIL EXISTS:The query is derived from the method name (existsBy + Email).
    boolean existsByEmail(String email);

    //checks if the user exists by email and password
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email AND u.password = :password")
    boolean existsByEmailAndPassword(String email, String password);

    // Query to get password by email
    @Query(value = "SELECT password FROM users WHERE email = :email", nativeQuery = true)
    String getPasswordByEmail(@Param("email") String email);
    @Query(value = "SELECT * FROM users WHERE uucms_id = :uucmsId", nativeQuery = true)
    User getUserByUucms_id(String uucmsId);
}
// END OF USER REPOSITORY INTERFACE.
