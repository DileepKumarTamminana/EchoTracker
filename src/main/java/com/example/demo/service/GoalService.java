package com.example.demo.service;

import com.example.demo.dto.GoalProgress;
import com.example.demo.model.Goal;
import com.example.demo.model.User;
import com.example.demo.repository.ActivityRepository;
import com.example.demo.repository.GoalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final ActivityRepository activityRepository;

    public GoalService(GoalRepository goalRepository, ActivityRepository activityRepository) {
        this.goalRepository = goalRepository;
        this.activityRepository = activityRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Goal> activeGoal(User user) {
        return goalRepository.findByUserAndActiveTrue(user);
    }

    /** Replaces any previous active goal with a new monthly target. */
    @Transactional
    public Goal setGoal(User user, double monthlyTargetKg) {
        goalRepository.findByUserAndActiveTrue(user).ifPresent(existing -> {
            existing.setActive(false);
            goalRepository.save(existing);
        });
        Goal goal = new Goal();
        goal.setUser(user);
        goal.setMonthlyTargetKg(monthlyTargetKg);
        goal.setActive(true);
        return goalRepository.save(goal);
    }

    @Transactional(readOnly = true)
    public GoalProgress progressThisMonth(User user) {
        YearMonth ym = YearMonth.now();
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        double used = round(activityRepository.totalCo2Between(user, start, end));

        Optional<Goal> goal = goalRepository.findByUserAndActiveTrue(user);
        if (goal.isEmpty()) {
            return new GoalProgress(false, 0, used, 0, false);
        }
        double target = goal.get().getMonthlyTargetKg();
        double percentRaw = target > 0 ? (used / target) * 100.0 : 0;
        double percentBar = Math.min(100, Math.round(percentRaw));
        return new GoalProgress(true, target, used, percentBar, used > target);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
