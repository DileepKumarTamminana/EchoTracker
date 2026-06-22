package com.example.demo.controller;

import com.example.demo.dto.TrendPoint;
import com.example.demo.model.User;
import com.example.demo.service.DashboardService;
import com.example.demo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
public class HistoryController {

    private final UserService userService;
    private final DashboardService dashboardService;

    public HistoryController(UserService userService, DashboardService dashboardService) {
        this.userService = userService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/history")
    public String history(@RequestParam(defaultValue = "day") String period,
                          Principal principal,
                          Model model) {
        User user = userService.requireByUsername(principal.getName());
        List<TrendPoint> series = dashboardService.history(user, period);
        double total = series.stream().mapToDouble(TrendPoint::co2Kg).sum();
        model.addAttribute("period", period);
        model.addAttribute("series", series);
        model.addAttribute("seriesLabels", series.stream().map(TrendPoint::label).toList());
        model.addAttribute("seriesValues", series.stream().map(TrendPoint::co2Kg).toList());
        model.addAttribute("periodTotal", Math.round(total * 100.0) / 100.0);
        return "history";
    }
}
