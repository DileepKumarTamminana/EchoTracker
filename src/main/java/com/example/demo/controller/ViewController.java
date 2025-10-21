package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.demo.model.Activity;
import com.example.demo.model.Emission;
import com.example.demo.model.User;
import com.example.demo.repository.ActivityRepository;
import com.example.demo.repository.EmissionRepository;
import com.example.demo.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class ViewController {
    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private EmissionRepository emissionRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping({ "/", "/login" })
    public String loginPage(Model model) {
        model.addAttribute("error", "");
        return "login";
    }

    @PostMapping({ "/", "/login" })
    public String login(@RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("user", user);
            return "redirect:/index";
        } else {
            model.addAttribute("error", "Invalid credentials");
            return "login";
        }
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @PostMapping("/activity/add")
    public String addActivity(@RequestParam String type,
            @RequestParam String amount,
            @RequestParam String date) {
        Activity activity = new Activity();
        activity.setType(type);
        activity.setActivityValue(Double.parseDouble(amount));
        activity.setDate(date);
        activity = activityRepository.save(activity);

        double emissionVal = calculateEmission(type, Double.parseDouble(amount));
        emissionVal = Math.round(emissionVal * 100.00) / 100.00; // Round to 2 decimal places
        Emission emission = new Emission();
        emission.setActivity(activity);
        emission.setEmissionValue(emissionVal);
        emission.setDate(date);
        emissionRepository.save(emission);

        return "redirect:/dashboard";
    }

    private double calculateEmission(String type, double amount) {
        Map<String, Double> idealPrices = Map.of(
                "car", 105.0, // per litre for petrol
                "electricity", 9.0, // per kWh
                "water", 0.1, // per litre
                "public_transport", 95.0, // per km
                "metro", 4.0); // per km
        Map<String, Double> emissionFactors = Map.of(
                "car", 2.31,
                "electricity", 0.92,
                "water", 0.0003,
                "public_transport", 2.68,
                "metro", 0.82);
        if (!idealPrices.containsKey(type) || !emissionFactors.containsKey(type))
            return 0.0;
        return (amount / idealPrices.get(type)) * emissionFactors.get(type);
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Activity> activities = activityRepository.findAll();
        double totalEmissions = 0;
        Map<String, Double> emissionsByCategory = new HashMap<>();
        for (Activity activity : activities) {
            String type = activity.getType();
            double amount = activity.getActivityValue();
            double emission = calculateEmission(type, amount);
            totalEmissions += emission;
            emissionsByCategory.put(type,
                    emissionsByCategory.getOrDefault(type, 0.0) + emission);
        }
        model.addAttribute("totalEmissions", totalEmissions);
        model.addAttribute("emissionsByCategory", emissionsByCategory);
        model.addAttribute("idealTarget", 50);
        return "dashboard";
    }

    @GetMapping("/suggestions")
    public String suggestions(Model model) {
        List<String> suggestions = Arrays.asList(
                "Use public transport or carpool instead of driving alone.",
                "Switch to LED bulbs and energy-efficient appliances.",
                "Eat more plant-based meals and reduce meat consumption.",
                "Unplug devices when not in use to save electricity.",
                "Recycle and reuse materials to reduce waste.",
                "Use a bicycle or walk for short distances.",
                "Install solar panels if feasible for your home.",
                "Buy local and seasonal produce to reduce transport emissions.",
                "Limit air travel or choose direct flights when possible.",
                "Reduce water usage by fixing leaks and using low-flow fixtures.",
                "Compost food scraps to reduce landfill waste.",
                "Wash clothes in cold water to save energy.",
                "Take shorter showers to conserve water.",
                "Plant trees or support reforestation projects.",
                "Opt for digital receipts and documents to save paper.");
        model.addAttribute("suggestions", suggestions);
        return "suggestions";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("error", "");
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            Model model) {
        if (userRepository.findByUsername(username) != null) {
            model.addAttribute("error", "Username already exists");
            return "register";
        }
        if (userRepository.findByEmail(email) != null) {
            model.addAttribute("error", "Email already exists");
            return "register";
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        userRepository.save(user);
        return "redirect:/login";
    }

    @GetMapping("/history")
    public String emissionHistory(
            @RequestParam(value = "period", required = false, defaultValue = "day") String period,
            Model model) {
        List<Emission> emissions = emissionRepository.findAll();
        Map<String, Double> history = new LinkedHashMap<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate now = LocalDate.now();

        if ("day".equals(period)) {
            for (int i = 0; i < 7; i++) {
                LocalDate date = now.minusDays(i);
                double sum = emissions.stream()
                        .filter(e -> e.getDate().equals(date.format(dtf)))
                        .mapToDouble(Emission::getEmissionValue)
                        .sum();
                history.put(date.format(dtf), sum);
            }
        } else if ("week".equals(period)) {
            for (int i = 0; i < 4; i++) {
                LocalDate start = now.minusWeeks(i).with(DayOfWeek.MONDAY);
                LocalDate end = start.plusDays(6);
                double sum = emissions.stream()
                        .filter(e -> {
                            LocalDate edate = LocalDate.parse(e.getDate(), dtf);
                            return !edate.isBefore(start) && !edate.isAfter(end);
                        })
                        .mapToDouble(Emission::getEmissionValue)
                        .sum();
                history.put("Week " + start.format(dtf) + " - " + end.format(dtf), sum);
            }
        } else if ("month".equals(period)) {
            for (int i = 0; i < 6; i++) {
                LocalDate date = now.minusMonths(i).withDayOfMonth(1);
                YearMonth ym = YearMonth.from(date);
                double sum = emissions.stream()
                        .filter(e -> YearMonth.from(LocalDate.parse(e.getDate(), dtf)).equals(ym))
                        .mapToDouble(Emission::getEmissionValue)
                        .sum();
                history.put(ym.toString(), sum);
            }
        }
        model.addAttribute("history", history);
        model.addAttribute("period", period);
        return "history";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}