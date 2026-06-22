package com.example.demo.dto;

/** Aggregated footprint for one category, used in dashboard breakdowns. */
public record CategoryStat(String label, double co2Kg, double percent) {
}
