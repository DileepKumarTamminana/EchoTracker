package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 60)
    private String username;

    @Column(unique = true, nullable = false, length = 120)
    private String email;

    /** BCrypt hash, never the raw password. */
    @Column(nullable = false)
    private String password;

    /** Optional display name shown around the UI. */
    @Column(length = 80)
    private String fullName;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
