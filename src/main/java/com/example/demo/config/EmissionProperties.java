package com.example.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Binds the {@code emission.factors.*} keys from application.properties into a
 * map of kg-CO2e-per-unit factors. Keys correspond to
 * {@link com.example.demo.model.ActivityType#factorKey()}.
 * Registered via {@code @EnableConfigurationProperties} on the main application.
 */
@ConfigurationProperties(prefix = "emission")
public class EmissionProperties {

    private Map<String, Double> factors = new HashMap<>();

    public Map<String, Double> getFactors() {
        return factors;
    }

    public void setFactors(Map<String, Double> factors) {
        this.factors = factors;
    }
}
