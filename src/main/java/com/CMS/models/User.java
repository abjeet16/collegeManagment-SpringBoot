package com.CMS.models;

import com.CMS.enums.Role;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Data
public class User {

    @Id
    @Column(name = "uucms_id")
    private String Uucms_id;
    private String first_name;
    private String last_name;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private Long phone;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.STUDENT; // Default role set to USER

    // New bidirectional relationship mapping
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentDetails> studentDetails = new ArrayList<>();

    public User(String uucms_id, String first_name, String last_name, String email, Long phone, String password, Role role) {
        Uucms_id = uucms_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
    }
}

