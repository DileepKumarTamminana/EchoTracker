package com.example.demo.repository;

import com.example.demo.model.Emission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmissionRepository extends JpaRepository<Emission, Long> {
}