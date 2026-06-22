package com.example.demo.controller;

import com.example.demo.dto.TrendPoint;
import com.example.demo.model.User;
import com.example.demo.service.DashboardService;
import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * Small JSON API consumed by the history page so changing the period updates the
 * chart without a full page reload.
 */
@RestController
@RequestMapping("/api/charts")
public class ChartApiController {

    private final UserService userService;
    private final DashboardService dashboardService;

    public ChartApiController(UserService userService, DashboardService dashboardService) {
        this.userService = userService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/history")
    public Map<String, Object> history(@RequestParam(defaultValue = "day") String period,
                                       Principal principal) {
        User user = userService.requireByUsername(principal.getName());
        List<TrendPoint> series = dashboardService.history(user, period);
        return Map.of(
                "period", period,
                "labels", series.stream().map(TrendPoint::label).toList(),
                "values", series.stream().map(TrendPoint::co2Kg).toList());
    }
}
