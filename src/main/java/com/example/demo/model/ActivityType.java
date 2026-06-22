package com.example.demo.model;

/**
 * Catalogue of trackable activities. Each value carries a human label, the unit
 * its amount is measured in, an emoji used in the UI, and the {@link Category}
 * it rolls up into for dashboard breakdowns. The actual kg-CO2e-per-unit factor
 * is configured in application.properties (keyed by {@link #name()} lower-cased)
 * so it can be tuned without recompiling.
 */
public enum ActivityType {

    CAR_PETROL("Car (petrol)", "km", "🚗", Category.TRANSPORT),
    CAR_DIESEL("Car (diesel)", "km", "🚙", Category.TRANSPORT),
    MOTORBIKE("Motorbike", "km", "🏍️", Category.TRANSPORT),
    BUS("Bus", "km", "🚌", Category.TRANSPORT),
    METRO("Metro / Tram", "km", "🚈", Category.TRANSPORT),
    TRAIN("Train", "km", "🚂", Category.TRANSPORT),
    FLIGHT("Flight", "km", "✈️", Category.TRANSPORT),

    ELECTRICITY("Electricity", "kWh", "⚡", Category.HOME),
    NATURAL_GAS("Natural gas", "kWh", "🔥", Category.HOME),
    LPG("LPG cylinder", "kg", "🫙", Category.HOME),
    WATER("Water", "litre", "💧", Category.HOME),

    WASTE("Waste to landfill", "kg", "🗑️", Category.LIFESTYLE),
    MEAT_MEAL("Meat-based meal", "meal", "🍖", Category.LIFESTYLE),
    VEG_MEAL("Vegetarian meal", "meal", "🥗", Category.LIFESTYLE);

    public enum Category {
        TRANSPORT("Transport"),
        HOME("Home & Energy"),
        LIFESTYLE("Lifestyle");

        private final String label;

        Category(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private final String label;
    private final String unit;
    private final String icon;
    private final Category category;

    ActivityType(String label, String unit, String icon, Category category) {
        this.label = label;
        this.unit = unit;
        this.icon = icon;
        this.category = category;
    }

    public String getLabel() {
        return label;
    }

    public String getUnit() {
        return unit;
    }

    public String getIcon() {
        return icon;
    }

    public Category getCategory() {
        return category;
    }

    /** Property key used to look up the emission factor (e.g. "car_petrol"). */
    public String factorKey() {
        return name().toLowerCase();
    }
}
