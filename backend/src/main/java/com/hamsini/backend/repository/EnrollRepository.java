package com.hamsini.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hamsini.backend.model.EnrollRequest;

public interface EnrollRepository extends JpaRepository<EnrollRequest, Long> {
}
