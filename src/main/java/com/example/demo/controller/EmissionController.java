package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;
import com.example.demo.model.*;
import com.example.demo.repository.EmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;

import com.example.demo.config.EmissionRates;

@RestController
@RequestMapping("/api/emission")
public class EmissionController {
    @Autowired
    private EmissionRepository emissionRepository;

    @Autowired
    private EmissionRates emissionRates;

    @PostMapping("/calculate")
    public Emission calculateEmission(@RequestBody Activity activity) {
        // Load rates from configuration (application.properties) so values can be updated without code changes
        Map<String, Double> idealPrices = emissionRates.getIdealPrices();
        Map<String, Double> emissionFactors = emissionRates.getEmissionFactors();

        String type = activity.getType();
        double amount = activity.getActivityValue();
        double emission = 0.0;
        if (idealPrices != null && emissionFactors != null && idealPrices.containsKey(type) && emissionFactors.containsKey(type)) {
            emission = (amount / idealPrices.get(type)) * emissionFactors.get(type);
        }
        Emission emissionObj = new Emission(type, emission, activity.getDate());
        emissionRepository.save(emissionObj);
        return emissionObj;
    }
}
