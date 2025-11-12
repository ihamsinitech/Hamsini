package com.hamsini.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hamsini.backend.model.AdminLogin;
import com.hamsini.backend.model.EnrollRequest;
import com.hamsini.backend.repository.EnrollRepository;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://127.0.0.1:5500") // allow only your frontend
public class AdminController {
    @Autowired
    private EnrollRepository enrollRepo;

    // ✅ Admin login endpoint
    @PostMapping("/admin/login")
    public ResponseEntity<String> adminLogin(@RequestBody AdminLogin login) {
        if ("admin@gmail.com".equals(login.getEmail()) && "admin123".equals(login.getPassword())) {
            return ResponseEntity.ok("Login Successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    // ✅ Get all enroll details (only for admin)
    @GetMapping("/admin/enrolls")
    public ResponseEntity<List<EnrollRequest>> getAllEnrolls() {
        List<EnrollRequest> enrolls = enrollRepo.findAll();
        return ResponseEntity.ok(enrolls);
    }
}



