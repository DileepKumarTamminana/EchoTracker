package com.example.demo.service;

import com.example.demo.dto.BadgeView;
import com.example.demo.model.*;
import com.example.demo.repository.AchievementRepository;
import com.example.demo.repository.ActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final ActivityRepository activityRepository;
    private final GoalService goalService;

    public AchievementService(AchievementRepository achievementRepository,
                              ActivityRepository activityRepository,
                              GoalService goalService) {
        this.achievementRepository = achievementRepository;
        this.activityRepository = activityRepository;
        this.goalService = goalService;
    }

    /**
     * Re-evaluates all badge rules for the user and persists any newly earned
     * badges. Returns the list of badges awarded by this call (may be empty).
     */
    @Transactional
    public List<Badge> evaluate(User user) {
        Set<Badge> qualified = qualifyingBadges(user);
        List<Badge> newlyEarned = new ArrayList<>();
        for (Badge badge : qualified) {
            if (!achievementRepository.existsByUserAndCode(user, badge.name())) {
                Achievement a = new Achievement();
                a.setUser(user);
                a.setCode(badge.name());
                achievementRepository.save(a);
                newlyEarned.add(badge);
            }
        }
        return newlyEarned;
    }

    private Set<Badge> qualifyingBadges(User user) {
        Set<Badge> earned = EnumSet.noneOf(Badge.class);

        long total = activityRepository.countByUser(user);
        if (total >= 1) earned.add(Badge.FIRST_STEP);
        if (total >= 10) earned.add(Badge.GETTING_STARTED);
        if (total >= 50) earned.add(Badge.DEDICATED);

        int streak = currentStreak(activityRepository.findDistinctDates(user));
        if (streak >= 3) earned.add(Badge.STREAK_3);
        if (streak >= 7) earned.add(Badge.STREAK_7);

        goalService.activeGoal(user).ifPresent(g -> earned.add(Badge.GOAL_SETTER));
        var progress = goalService.progressThisMonth(user);
        if (progress.hasGoal() && progress.usedKg() > 0 && !progress.exceeded()) {
            earned.add(Badge.UNDER_BUDGET);
        }

        long greenTrips = activityRepository.countByUserAndType(user, ActivityType.METRO)
                + activityRepository.countByUserAndType(user, ActivityType.TRAIN)
                + activityRepository.countByUserAndType(user, ActivityType.BUS);
        if (greenTrips >= 1) earned.add(Badge.GREEN_COMMUTER);

        if (activityRepository.countByUserAndType(user, ActivityType.VEG_MEAL) >= 5) {
            earned.add(Badge.PLANT_POWERED);
        }
        return earned;
    }

    /** Length of the run of consecutive days ending today or yesterday. */
    public int currentStreak(List<LocalDate> distinctDatesDesc) {
        if (distinctDatesDesc.isEmpty()) {
            return 0;
        }
        LocalDate today = LocalDate.now();
        LocalDate first = distinctDatesDesc.get(0);
        if (first.isBefore(today.minusDays(1))) {
            return 0; // streak already broken
        }
        int streak = 1;
        LocalDate previous = first;
        for (int i = 1; i < distinctDatesDesc.size(); i++) {
            LocalDate d = distinctDatesDesc.get(i);
            if (d.equals(previous.minusDays(1))) {
                streak++;
                previous = d;
            } else {
                break;
            }
        }
        return streak;
    }

    @Transactional(readOnly = true)
    public List<BadgeView> badgeBoard(User user) {
        var earned = achievementRepository.findByUserOrderByEarnedAtDesc(user);
        List<BadgeView> board = new ArrayList<>();
        for (Badge badge : Badge.values()) {
            var match = earned.stream()
                    .filter(a -> a.getCode().equals(badge.name()))
                    .findFirst();
            board.add(new BadgeView(badge, match.isPresent(),
                    match.map(Achievement::getEarnedAt).orElse(null)));
        }
        return board;
    }

    @Transactional(readOnly = true)
    public long earnedCount(User user) {
        return achievementRepository.findByUserOrderByEarnedAtDesc(user).size();
    }
}
