package com.example.demo.controller;

import com.example.demo.dto.ActivityForm;
import com.example.demo.model.ActivityType;
import com.example.demo.model.User;
import com.example.demo.service.ActivityService;
import com.example.demo.service.AchievementService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/activities")
public class ActivityController {

    private final UserService userService;
    private final ActivityService activityService;
    private final AchievementService achievementService;

    public ActivityController(UserService userService,
                              ActivityService activityService,
                              AchievementService achievementService) {
        this.userService = userService;
        this.activityService = activityService;
        this.achievementService = achievementService;
    }

    @ModelAttribute("allTypes")
    public ActivityType[] allTypes() {
        return ActivityType.values();
    }

    @GetMapping
    public String list(Principal principal, Model model) {
        User user = userService.requireByUsername(principal.getName());
        if (!model.containsAttribute("activityForm")) {
            model.addAttribute("activityForm", new ActivityForm());
        }
        model.addAttribute("activities", activityService.listFor(user));
        return "activities";
    }

    @PostMapping
    public String add(@Valid @ModelAttribute("activityForm") ActivityForm form,
                      BindingResult result,
                      Principal principal,
                      Model model,
                      RedirectAttributes redirectAttributes) {
        User user = userService.requireByUsername(principal.getName());
        if (result.hasErrors()) {
            model.addAttribute("activities", activityService.listFor(user));
            return "activities";
        }
        var saved = activityService.add(user, form);
        var newBadges = achievementService.evaluate(user);
        redirectAttributes.addFlashAttribute("flashSuccess",
                String.format("Logged %s %s — %.2f kg CO₂e added.",
                        trimNumber(saved.getAmount()), saved.getType().getUnit(), saved.getCo2Kg()));
        if (!newBadges.isEmpty()) {
            redirectAttributes.addFlashAttribute("flashBadges",
                    newBadges.stream().map(b -> b.getIcon() + " " + b.getTitle()).toList());
        }
        return "redirect:/activities";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         Principal principal,
                         RedirectAttributes redirectAttributes) {
        User user = userService.requireByUsername(principal.getName());
        if (activityService.delete(user, id)) {
            redirectAttributes.addFlashAttribute("flashSuccess", "Activity deleted.");
        } else {
            redirectAttributes.addFlashAttribute("flashError", "Could not delete that activity.");
        }
        return "redirect:/activities";
    }

    private String trimNumber(double value) {
        if (value == Math.floor(value)) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }
}
