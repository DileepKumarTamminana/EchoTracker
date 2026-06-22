package com.example.demo.service;

import com.example.demo.model.Activity;
import com.example.demo.model.ActivityType;
import com.example.demo.model.User;
import com.example.demo.repository.ActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Produces eco tips. The first tips are personalised to the user's biggest
 * emission category; the rest are general best-practice advice.
 */
@Service
public class SuggestionService {

    private static final Map<ActivityType.Category, List<String>> TARGETED = Map.of(
            ActivityType.Category.TRANSPORT, List.of(
                    "Your transport footprint is highest — try combining trips or carpooling.",
                    "Swap one car journey a week for metro, train or cycling.",
                    "For trips under 3 km, walking or biking emits virtually nothing."),
            ActivityType.Category.HOME, List.of(
                    "Home energy leads your emissions — switch to LED bulbs and efficient appliances.",
                    "Lower your geyser/AC by a couple of degrees to cut electricity use.",
                    "Unplug idle devices; standby power adds up over a month."),
            ActivityType.Category.LIFESTYLE, List.of(
                    "Lifestyle is your top category — try a few more plant-based meals each week.",
                    "Reduce food waste: plan meals and compost scraps.",
                    "Buy local and seasonal produce to cut transport emissions."));

    private static final List<String> GENERAL = List.of(
            "Recycle and reuse materials to reduce landfill waste.",
            "Take shorter showers and fix leaks to conserve water.",
            "Wash clothes in cold water and air-dry when possible.",
            "Choose direct flights and limit air travel where you can.",
            "Support reforestation or plant trees in your community.",
            "Opt for digital receipts and documents to save paper.",
            "Install solar panels if feasible for your home.");

    private final ActivityRepository activityRepository;

    public SuggestionService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Transactional(readOnly = true)
    public List<String> suggestionsFor(User user) {
        List<Activity> all = activityRepository.findByUserOrderByDateDescIdDesc(user);
        List<String> result = new ArrayList<>();

        if (!all.isEmpty()) {
            Map<ActivityType.Category, Double> byCategory = new EnumMap<>(ActivityType.Category.class);
            for (Activity a : all) {
                byCategory.merge(a.getType().getCategory(), a.getCo2Kg(), Double::sum);
            }
            byCategory.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .ifPresent(top -> result.addAll(TARGETED.getOrDefault(top, List.of())));
        }
        result.addAll(GENERAL);
        return result;
    }
}
