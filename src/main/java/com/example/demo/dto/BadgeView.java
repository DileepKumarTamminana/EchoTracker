package com.example.demo.dto;

import com.example.demo.model.Badge;

import java.time.Instant;

/** A badge plus whether the current user has earned it (and when). */
public record BadgeView(Badge badge, boolean earned, Instant earnedAt) {
}
