package com.example.demo.service;

import com.example.demo.dto.CategoryStat;
import com.example.demo.dto.TrendPoint;
import com.example.demo.model.Activity;
import com.example.demo.model.ActivityType;
import com.example.demo.model.User;
import com.example.demo.repository.ActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Read-only aggregation queries powering the dashboard and history pages.
 */
@Service
public class DashboardService {

    private static final DateTimeFormatter DAY_LABEL = DateTimeFormatter.ofPattern("dd MMM");
    private static final DateTimeFormatter MONTH_LABEL = DateTimeFormatter.ofPattern("MMM yyyy");

    private final ActivityRepository activityRepository;

    public DashboardService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Transactional(readOnly = true)
    public double totalAllTime(User user) {
        return round(activityRepository.totalCo2(user));
    }

    @Transactional(readOnly = true)
    public double totalThisMonth(User user) {
        YearMonth ym = YearMonth.now();
        return round(activityRepository.totalCo2Between(user, ym.atDay(1), ym.atEndOfMonth()));
    }

    /** Breakdown by category across all logged activities, largest first. */
    @Transactional(readOnly = true)
    public List<CategoryStat> categoryBreakdown(User user) {
        List<Activity> all = activityRepository.findByUserOrderByDateDescIdDesc(user);
        Map<ActivityType.Category, Double> byCategory = new EnumMap<>(ActivityType.Category.class);
        double total = 0;
        for (Activity a : all) {
            byCategory.merge(a.getType().getCategory(), a.getCo2Kg(), Double::sum);
            total += a.getCo2Kg();
        }
        double finalTotal = total;
        List<CategoryStat> stats = new ArrayList<>();
        for (var entry : byCategory.entrySet()) {
            double pct = finalTotal > 0 ? Math.round((entry.getValue() / finalTotal) * 1000.0) / 10.0 : 0;
            stats.add(new CategoryStat(entry.getKey().getLabel(), round(entry.getValue()), pct));
        }
        stats.sort(Comparator.comparingDouble(CategoryStat::co2Kg).reversed());
        return stats;
    }

    /** Footprint per activity type (all-time) as label->kg, largest first. */
    @Transactional(readOnly = true)
    public LinkedHashMap<String, Double> byType(User user) {
        List<Activity> all = activityRepository.findByUserOrderByDateDescIdDesc(user);
        Map<ActivityType, Double> map = new EnumMap<>(ActivityType.class);
        for (Activity a : all) {
            map.merge(a.getType(), a.getCo2Kg(), Double::sum);
        }
        return map.entrySet().stream()
                .sorted(Map.Entry.<ActivityType, Double>comparingByValue().reversed())
                .collect(LinkedHashMap::new,
                        (m, e) -> m.put(e.getKey().getLabel(), round(e.getValue())),
                        LinkedHashMap::putAll);
    }

    /** Total CO2e for each of the last {@code months} calendar months, oldest first. */
    @Transactional(readOnly = true)
    public List<TrendPoint> monthlyTrend(User user, int months) {
        List<TrendPoint> points = new ArrayList<>();
        YearMonth current = YearMonth.now();
        for (int i = months - 1; i >= 0; i--) {
            YearMonth ym = current.minusMonths(i);
            double sum = activityRepository.totalCo2Between(user, ym.atDay(1), ym.atEndOfMonth());
            points.add(new TrendPoint(ym.format(MONTH_LABEL), round(sum)));
        }
        return points;
    }

    /**
     * History series for the requested period:
     * day = last 14 days, week = last 8 weeks, month = last 12 months.
     */
    @Transactional(readOnly = true)
    public List<TrendPoint> history(User user, String period) {
        LocalDate today = LocalDate.now();
        List<TrendPoint> points = new ArrayList<>();
        switch (period == null ? "day" : period) {
            case "week" -> {
                for (int i = 7; i >= 0; i--) {
                    LocalDate start = today.minusWeeks(i).with(DayOfWeek.MONDAY);
                    LocalDate end = start.plusDays(6);
                    double sum = activityRepository.totalCo2Between(user, start, end);
                    points.add(new TrendPoint(start.format(DAY_LABEL), round(sum)));
                }
            }
            case "month" -> {
                return monthlyTrend(user, 12);
            }
            default -> {
                for (int i = 13; i >= 0; i--) {
                    LocalDate d = today.minusDays(i);
                    double sum = activityRepository.totalCo2Between(user, d, d);
                    points.add(new TrendPoint(d.format(DAY_LABEL), round(sum)));
                }
            }
        }
        return points;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
