package com.example.demo.dto;

/**
 * This-month progress against the user's active goal.
 *
 * @param hasGoal   whether a goal is set
 * @param targetKg  monthly budget
 * @param usedKg    consumed so far this month
 * @param percent   used/target as a 0-100+ percentage (capped at 100 for the bar)
 * @param exceeded  whether the budget has been blown
 */
public record GoalProgress(boolean hasGoal, double targetKg, double usedKg,
                           double percent, boolean exceeded) {

    public double remainingKg() {
        return Math.max(0, targetKg - usedKg);
    }
}
