package com.example.demo.dto;

import com.example.demo.model.ActivityType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class ActivityForm {

    @NotNull(message = "Choose an activity type")
    private ActivityType type;

    @NotNull(message = "Enter an amount")
    @Positive(message = "Amount must be greater than zero")
    private Double amount;

    @NotNull(message = "Pick a date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date = LocalDate.now();

    @Size(max = 250, message = "Note is too long")
    private String note;
}
