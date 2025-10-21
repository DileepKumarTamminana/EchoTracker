package com.example.demo.model;

import jakarta.persistence.*;

@Entity
public class Emission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;
    private double emissionValue;
    private String date;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // Default no-argument constructor required by JPA
    public Emission() {
    }

    // Constructor matching the required signature
    public Emission(String type, double emissionValue, String date) {
        this.type = type;
        this.emissionValue = emissionValue;
        this.date = date;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public double getEmissionValue() {
        return emissionValue;
    }

    public void setEmissionValue(double emissionValue) {
        this.emissionValue = emissionValue;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}