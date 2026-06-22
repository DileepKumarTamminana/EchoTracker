package com.example.demo.controller;

import com.example.demo.dto.RegisterForm;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        if (!model.containsAttribute("registerForm")) {
            model.addAttribute("registerForm", new RegisterForm());
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerForm") RegisterForm form,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (userService.usernameTaken(form.getUsername())) {
            result.rejectValue("username", "taken", "That username is already taken");
        }
        if (userService.emailTaken(form.getEmail())) {
            result.rejectValue("email", "taken", "That email is already registered");
        }
        if (result.hasErrors()) {
            return "register";
        }
        userService.register(form);
        redirectAttributes.addFlashAttribute("registered", true);
        return "redirect:/login?registered";
    }
}
