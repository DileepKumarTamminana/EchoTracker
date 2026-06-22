package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * A user's monthly carbon budget. Only one active goal per user at a time;
 * older goals are kept (active=false) for history.
 */
@Entity
@Table(name = "goals")
@Getter
@Setter
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Target monthly footprint in kg CO2e. */
    @Column(nullable = false)
    private double monthlyTargetKg;

    @Column(nullable = false)
    private boolean active = true;
}
