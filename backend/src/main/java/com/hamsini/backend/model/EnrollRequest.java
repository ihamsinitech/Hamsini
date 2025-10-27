package com.hamsini.backend.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "enroll_request") // Match your actual table name
@NoArgsConstructor
@AllArgsConstructor
public class EnrollRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    private String course;
    @Column(name = "full_name")
    private String fullName;

    private String mobile;
    
    private String email;
    
    private String batch;

    
}
