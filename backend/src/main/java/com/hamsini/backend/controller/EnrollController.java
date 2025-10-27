package com.hamsini.backend.controller;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hamsini.backend.model.EnrollRequest;
import com.hamsini.backend.repository.EnrollRepository;


@RestController
@RequestMapping("/api/enroll")
@CrossOrigin(origins = "*")
public class EnrollController {
     @Autowired
    private EnrollRepository enrollRepo;

    @PostMapping
    public ResponseEntity<?> enrollUser(@RequestBody EnrollRequest enroll) {
        // 🛠 Log incoming data
        System.out.println("📩 Received Enroll Request: " + enroll);

        // Save in DB
        EnrollRequest saved = enrollRepo.save(enroll);

        // 🛠 Log saved data (to confirm Hibernate ran insert)
        System.out.println("✅ Saved Enroll Request: " + saved);

        return ResponseEntity.ok("Enrollment Successful!");
    }

    
}

        


