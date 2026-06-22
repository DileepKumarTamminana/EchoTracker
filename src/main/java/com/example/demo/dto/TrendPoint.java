package com.example.demo.dto;

/** A single labelled point on a time-series chart (e.g. a day or month total). */
public record TrendPoint(String label, double co2Kg) {
}
