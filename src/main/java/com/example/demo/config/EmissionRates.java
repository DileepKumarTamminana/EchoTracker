package com.example.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "emission")
public class EmissionRates {
    private Map<String, Double> ideal = new HashMap<>();
    private Map<String, Double> factor = new HashMap<>();

    public Map<String, Double> getIdeal() {
        return ideal;
    }

    public void setIdeal(Map<String, Double> ideal) {
        this.ideal = ideal;
    }

    public Map<String, Double> getFactor() {
        return factor;
    }

    public void setFactor(Map<String, Double> factor) {
        this.factor = factor;
    }

    // Convenience getters matching previous code keys
    public Map<String, Double> getIdealPrices() {
        return ideal;
    }

    public Map<String, Double> getEmissionFactors() {
        return factor;
    }
}
