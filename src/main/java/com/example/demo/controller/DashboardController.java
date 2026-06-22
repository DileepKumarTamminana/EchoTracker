package com.example.demo.controller;

import com.example.demo.dto.CategoryStat;
import com.example.demo.dto.TrendPoint;
import com.example.demo.model.User;
import com.example.demo.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class DashboardController {

    private final UserService userService;
    private final DashboardService dashboardService;
    private final GoalService goalService;
    private final AchievementService achievementService;
    private final ActivityService activityService;

    public DashboardController(UserService userService,
                               DashboardService dashboardService,
                               GoalService goalService,
                               AchievementService achievementService,
                               ActivityService activityService) {
        this.userService = userService;
        this.dashboardService = dashboardService;
        this.goalService = goalService;
        this.achievementService = achievementService;
        this.activityService = activityService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        User user = userService.requireByUsername(principal.getName());

        // Keep badges current whenever the user views their dashboard.
        achievementService.evaluate(user);

        List<CategoryStat> categoryStats = dashboardService.categoryBreakdown(user);
        var byType = dashboardService.byType(user);
        List<TrendPoint> trend = dashboardService.monthlyTrend(user, 6);

        model.addAttribute("totalAllTime", dashboardService.totalAllTime(user));
        model.addAttribute("totalThisMonth", dashboardService.totalThisMonth(user));
        model.addAttribute("goalProgress", goalService.progressThisMonth(user));
        model.addAttribute("streak", achievementService.currentStreak(
                activityService.listFor(user).stream().map(a -> a.getDate()).distinct().sorted(java.util.Comparator.reverseOrder()).toList()));
        model.addAttribute("earnedCount", achievementService.earnedCount(user));
        model.addAttribute("badges", achievementService.badgeBoard(user));
        model.addAttribute("recent", activityService.listFor(user).stream().limit(6).toList());

        // Chart-ready primitives (reliable for Thymeleaf JS inlining).
        model.addAttribute("categoryStats", categoryStats);
        model.addAttribute("catLabels", categoryStats.stream().map(CategoryStat::label).toList());
        model.addAttribute("catValues", categoryStats.stream().map(CategoryStat::co2Kg).toList());
        model.addAttribute("typeLabels", byType.keySet().stream().toList());
        model.addAttribute("typeValues", byType.values().stream().toList());
        model.addAttribute("trendLabels", trend.stream().map(TrendPoint::label).toList());
        model.addAttribute("trendValues", trend.stream().map(TrendPoint::co2Kg).toList());

        return "dashboard";
    }
}
