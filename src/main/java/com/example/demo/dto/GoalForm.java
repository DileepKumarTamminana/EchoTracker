package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoalForm {

    @NotNull(message = "Enter a monthly target")
    @Positive(message = "Target must be greater than zero")
    private Double monthlyTargetKg;
}
