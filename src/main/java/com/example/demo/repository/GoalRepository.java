package com.example.demo.repository;

import com.example.demo.model.Goal;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    Optional<Goal> findByUserAndActiveTrue(User user);
}
