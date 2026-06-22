package com.example.demo.controller;

import com.example.demo.dto.GoalForm;
import com.example.demo.model.User;
import com.example.demo.service.AchievementService;
import com.example.demo.service.GoalService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class GoalController {

    private final UserService userService;
    private final GoalService goalService;
    private final AchievementService achievementService;

    public GoalController(UserService userService,
                          GoalService goalService,
                          AchievementService achievementService) {
        this.userService = userService;
        this.goalService = goalService;
        this.achievementService = achievementService;
    }

    @GetMapping("/goals")
    public String goals(Principal principal, Model model) {
        User user = userService.requireByUsername(principal.getName());
        if (!model.containsAttribute("goalForm")) {
            GoalForm form = new GoalForm();
            goalService.activeGoal(user).ifPresent(g -> form.setMonthlyTargetKg(g.getMonthlyTargetKg()));
            model.addAttribute("goalForm", form);
        }
        model.addAttribute("goalProgress", goalService.progressThisMonth(user));
        return "goals";
    }

    @PostMapping("/goals")
    public String setGoal(@Valid @ModelAttribute("goalForm") GoalForm form,
                          BindingResult result,
                          Principal principal,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        User user = userService.requireByUsername(principal.getName());
        if (result.hasErrors()) {
            model.addAttribute("goalProgress", goalService.progressThisMonth(user));
            return "goals";
        }
        goalService.setGoal(user, form.getMonthlyTargetKg());
        achievementService.evaluate(user);
        redirectAttributes.addFlashAttribute("flashSuccess",
                String.format("Monthly target set to %.0f kg CO₂e.", form.getMonthlyTargetKg()));
        return "redirect:/goals";
    }
}
