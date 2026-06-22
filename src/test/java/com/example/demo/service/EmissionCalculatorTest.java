package com.example.demo.service;

import com.example.demo.config.EmissionProperties;
import com.example.demo.model.ActivityType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class EmissionCalculatorTest {

    private EmissionCalculator calculatorWith(Map<String, Double> factors) {
        EmissionProperties props = new EmissionProperties();
        props.setFactors(factors);
        return new EmissionCalculator(props);
    }

    @Test
    void multipliesAmountByConfiguredFactorAndRounds() {
        EmissionCalculator calc = calculatorWith(Map.of("car_petrol", 0.171));
        // 30 km * 0.171 = 5.13
        assertThat(calc.calculate(ActivityType.CAR_PETROL, 30)).isEqualTo(5.13);
    }

    @Test
    void unknownFactorYieldsZero() {
        EmissionCalculator calc = calculatorWith(Map.of());
        assertThat(calc.calculate(ActivityType.METRO, 100)).isZero();
    }
}
