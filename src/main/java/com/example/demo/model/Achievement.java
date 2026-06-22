package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * A badge earned by a user. The {@code code} maps to a {@link Badge} definition
 * and is unique per user so a badge is only awarded once.
 */
@Entity
@Table(name = "achievements", uniqueConstraints = {
        @UniqueConstraint(name = "uq_user_badge", columnNames = {"user_id", "code"})
})
@Getter
@Setter
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 40)
    private String code;

    @Column(nullable = false)
    private Instant earnedAt = Instant.now();

    @PrePersist
    void onCreate() {
        if (earnedAt == null) {
            earnedAt = Instant.now();
        }
    }
}
