package com.example.demo.service;

import com.example.demo.dto.ActivityForm;
import com.example.demo.model.Activity;
import com.example.demo.model.User;
import com.example.demo.repository.ActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final EmissionCalculator calculator;

    public ActivityService(ActivityRepository activityRepository, EmissionCalculator calculator) {
        this.activityRepository = activityRepository;
        this.calculator = calculator;
    }

    @Transactional
    public Activity add(User user, ActivityForm form) {
        Activity activity = new Activity();
        activity.setUser(user);
        activity.setType(form.getType());
        activity.setAmount(form.getAmount());
        activity.setDate(form.getDate() != null ? form.getDate() : LocalDate.now());
        activity.setNote(form.getNote());
        activity.setCo2Kg(calculator.calculate(form.getType(), form.getAmount()));
        return activityRepository.save(activity);
    }

    @Transactional(readOnly = true)
    public List<Activity> listFor(User user) {
        return activityRepository.findByUserOrderByDateDescIdDesc(user);
    }

    /** Deletes the activity only if it belongs to the given user. */
    @Transactional
    public boolean delete(User user, Long id) {
        return activityRepository.findById(id)
                .filter(a -> a.getUser().getId().equals(user.getId()))
                .map(a -> {
                    activityRepository.delete(a);
                    return true;
                })
                .orElse(false);
    }
}
