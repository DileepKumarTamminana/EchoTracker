package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

/**
 * Exposes the logged-in {@link User} to every Thymeleaf view as {@code currentUser}
 * so templates (navbar, greeting) don't each have to fetch it.
 */
@ControllerAdvice(basePackages = "com.example.demo.controller")
public class GlobalModelAdvice {

    private final UserService userService;

    public GlobalModelAdvice(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("currentUser")
    public User currentUser(Principal principal) {
        if (principal == null) {
            return null;
        }
        try {
            return userService.requireByUsername(principal.getName());
        } catch (RuntimeException ex) {
            return null;
        }
    }
}
