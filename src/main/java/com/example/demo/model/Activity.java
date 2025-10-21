package com.example.demo.model;

import jakarta.persistence.*;

@Entity
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    private double activityValue; // Use a non-reserved name

    private String date;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getActivityValue() {
        return activityValue;
    }

    public void setActivityValue(double activityValue) {
        this.activityValue = activityValue;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}