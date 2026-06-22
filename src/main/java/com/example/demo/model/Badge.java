package com.example.demo.model;

/**
 * Catalogue of earnable badges. The {@code code} stored on an {@link Achievement}
 * matches {@link #name()}; rules for awarding them live in the achievement service.
 */
public enum Badge {

    FIRST_STEP("First Step", "Logged your very first activity", "🌱"),
    GETTING_STARTED("Getting Started", "Logged 10 activities", "📈"),
    DEDICATED("Dedicated Tracker", "Logged 50 activities", "🏆"),
    STREAK_3("On a Roll", "Logged activities 3 days in a row", "🔥"),
    STREAK_7("Week Warrior", "Logged activities 7 days in a row", "⚡"),
    GOAL_SETTER("Goal Setter", "Set a monthly carbon budget", "🎯"),
    UNDER_BUDGET("Under Budget", "Stayed under your monthly goal", "✅"),
    GREEN_COMMUTER("Green Commuter", "Logged a low-carbon trip (metro/train/bus)", "🚈"),
    PLANT_POWERED("Plant Powered", "Logged 5 vegetarian meals", "🥗");

    private final String title;
    private final String description;
    private final String icon;

    Badge(String title, String description, String icon) {
        this.title = title;
        this.description = description;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }
}
