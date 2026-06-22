package com.example.demo.service;

import com.example.demo.config.EmissionProperties;
import com.example.demo.model.ActivityType;
import org.springframework.stereotype.Service;

/**
 * Converts an activity amount into kg CO2e using the configured per-unit factors.
 */
@Service
public class EmissionCalculator {

    private final EmissionProperties properties;

    public EmissionCalculator(EmissionProperties properties) {
        this.properties = properties;
    }

    /** kg CO2e for {@code amount} units of {@code type}, rounded to 2 decimals. */
    public double calculate(ActivityType type, double amount) {
        double factor = factorFor(type);
        return round(amount * factor);
    }

    public double factorFor(ActivityType type) {
        return properties.getFactors().getOrDefault(type.factorKey(), 0.0);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
