package com.example.demo.controller;

import com.example.demo.dto.ProfileForm;
import com.example.demo.model.User;
import com.example.demo.service.AchievementService;
import com.example.demo.service.DashboardService;
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
public class ProfileController {

    private final UserService userService;
    private final DashboardService dashboardService;
    private final AchievementService achievementService;

    public ProfileController(UserService userService,
                             DashboardService dashboardService,
                             AchievementService achievementService) {
        this.userService = userService;
        this.dashboardService = dashboardService;
        this.achievementService = achievementService;
    }

    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        User user = userService.requireByUsername(principal.getName());
        if (!model.containsAttribute("profileForm")) {
            ProfileForm form = new ProfileForm();
            form.setFullName(user.getFullName());
            model.addAttribute("profileForm", form);
        }
        model.addAttribute("totalAllTime", dashboardService.totalAllTime(user));
        model.addAttribute("badges", achievementService.badgeBoard(user));
        model.addAttribute("earnedCount", achievementService.earnedCount(user));
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("profileForm") ProfileForm form,
                                BindingResult result,
                                Principal principal,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        User user = userService.requireByUsername(principal.getName());
        if (result.hasErrors()) {
            model.addAttribute("totalAllTime", dashboardService.totalAllTime(user));
            model.addAttribute("badges", achievementService.badgeBoard(user));
            model.addAttribute("earnedCount", achievementService.earnedCount(user));
            return "profile";
        }
        userService.updateProfile(user, form);
        redirectAttributes.addFlashAttribute("flashSuccess", "Profile updated.");
        return "redirect:/profile";
    }
}
