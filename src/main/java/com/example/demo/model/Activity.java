package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

/**
 * A single logged activity together with its computed carbon footprint.
 * The {@code co2Kg} value is calculated once at save time so dashboards and
 * history queries never have to recompute it.
 */
@Entity
@Table(name = "activities", indexes = {
        @Index(name = "idx_activity_user_date", columnList = "user_id,date")
})
@Getter
@Setter
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ActivityType type;

    /** Amount in the activity type's unit (km, kWh, litre, meal...). */
    @Column(nullable = false)
    private double amount;

    /** Computed kg CO2e for this activity. */
    @Column(nullable = false)
    private double co2Kg;

    @Column(nullable = false)
    private LocalDate date;

    @Column(length = 250)
    private String note;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
