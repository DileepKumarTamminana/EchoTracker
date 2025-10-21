package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;
import com.example.demo.model.*;
import com.example.demo.repository.EmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@RestController
@RequestMapping("/api/emission")
public class EmissionController {
    @Autowired
    private EmissionRepository emissionRepository;

    @PostMapping("/calculate")
    public Emission calculateEmission(@RequestBody Activity activity) {
        Map<String, Double> idealPrices = Map.of(
                "car", 100.0,
                "electricity", 8.0,
                "water", 0.05,
                "public_transport", 90.0,
                "metro", 8.0);
        Map<String, Double> emissionFactors = Map.of(
                "car", 2.31,
                "electricity", 0.92,
                "water", 0.0003,
                "public_transport", 2.68,
                "metro", 0.92);
        String type = activity.getType();
        double amount = activity.getActivityValue();
        double emission = 0.0;
        if (idealPrices.containsKey(type) && emissionFactors.containsKey(type)) {
            emission = (amount / idealPrices.get(type)) * emissionFactors.get(type);
        }
        Emission emissionObj = new Emission(type, emission, activity.getDate());
        emissionRepository.save(emissionObj);
        return emissionObj;
    }
}
