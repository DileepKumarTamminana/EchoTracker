package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;
import com.example.demo.model.*;
import com.example.demo.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {
    @Autowired
    private ActivityRepository activityRepository;

    @PostMapping("/add")
    public String addActivity(@RequestBody Activity activity) {
        activityRepository.save(activity);
        return "Activity added";
    }

    @GetMapping("/all")
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }
}
