package com.mynotes.models;

import com.mynotes.enums.Department;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class TeacherDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Foreign key column for User
    private User user;

    @Enumerated(EnumType.STRING)
    private Department department;

    public void setDepartment(String departmentString) {
        switch (departmentString.toUpperCase()) { // Convert input to uppercase for matching
            case "BCA":
                this.department = Department.BCA;
                break;
            case "BBA":
                this.department = Department.BBA;
                break;
            case "MCA":
                this.department = Department.MCA;
                break;
            case "MBA":
                this.department = Department.MBA;
                break;
            case "BA":
                this.department = Department.BA;
                break;
            case "BCOM":
                this.department = Department.Bcom;
                break;
            default:
                throw new IllegalArgumentException("Invalid department: " + departmentString);
        }
    }
}
