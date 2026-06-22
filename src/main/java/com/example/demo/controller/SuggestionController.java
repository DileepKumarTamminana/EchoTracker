package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.SuggestionService;
import com.example.demo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class SuggestionController {

    private final UserService userService;
    private final SuggestionService suggestionService;

    public SuggestionController(UserService userService, SuggestionService suggestionService) {
        this.userService = userService;
        this.suggestionService = suggestionService;
    }

    @GetMapping("/suggestions")
    public String suggestions(Principal principal, Model model) {
        User user = userService.requireByUsername(principal.getName());
        model.addAttribute("suggestions", suggestionService.suggestionsFor(user));
        return "suggestions";
    }
}
