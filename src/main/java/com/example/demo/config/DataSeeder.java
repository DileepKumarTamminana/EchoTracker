package com.example.demo.config;

import com.example.demo.dto.ActivityForm;
import com.example.demo.dto.RegisterForm;
import com.example.demo.model.ActivityType;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AchievementService;
import com.example.demo.service.ActivityService;
import com.example.demo.service.GoalService;
import com.example.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Seeds a demo account (demo / demo123) with sample activities, a goal and the
 * badges it qualifies for, but only the first time the database is empty.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final UserService userService;
    private final ActivityService activityService;
    private final GoalService goalService;
    private final AchievementService achievementService;

    public DataSeeder(UserRepository userRepository,
                      UserService userService,
                      ActivityService activityService,
                      GoalService goalService,
                      AchievementService achievementService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.activityService = activityService;
        this.goalService = goalService;
        this.achievementService = achievementService;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }
        log.info("Seeding demo data (login: demo / demo123)...");

        RegisterForm reg = new RegisterForm();
        reg.setUsername("demo");
        reg.setEmail("demo@echotracker.app");
        reg.setPassword("demo123");
        reg.setFullName("Demo User");
        User user = userService.register(reg);

        LocalDate today = LocalDate.now();
        seed(user, ActivityType.CAR_PETROL, 32, today.minusDays(1), "Commute to office");
        seed(user, ActivityType.METRO, 18, today.minusDays(1), "Evening trip");
        seed(user, ActivityType.ELECTRICITY, 9.5, today.minusDays(2), "Home usage");
        seed(user, ActivityType.VEG_MEAL, 2, today.minusDays(2), null);
        seed(user, ActivityType.BUS, 12, today.minusDays(3), null);
        seed(user, ActivityType.CAR_PETROL, 28, today.minusDays(4), "Weekend errands");
        seed(user, ActivityType.ELECTRICITY, 8.0, today.minusDays(5), null);
        seed(user, ActivityType.TRAIN, 120, today.minusDays(12), "Intercity travel");
        seed(user, ActivityType.WATER, 200, today.minusDays(20), null);
        seed(user, ActivityType.MEAT_MEAL, 1, today.minusDays(35), null);

        goalService.setGoal(user, 150);
        achievementService.evaluate(user);
        log.info("Demo data seeded with {} sample activities.", 10);
    }

    private void seed(User user, ActivityType type, double amount, LocalDate date, String note) {
        ActivityForm form = new ActivityForm();
        form.setType(type);
        form.setAmount(amount);
        form.setDate(date);
        form.setNote(note);
        activityService.add(user, form);
    }
}
