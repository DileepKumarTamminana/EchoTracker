package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;
import com.example.demo.model.*;
import com.example.demo.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

@RestController
@RequestMapping("/api/user")
public class UserController {

    // In your controller (e.g., AuthController)
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public String register(@RequestParam String username,
            @RequestParam String email,
            @RequestParam String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        userRepository.save(user); // This should save the user
        return "redirect:/";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("user", user);
            return "redirect:/index";
        }
        model.addAttribute("error", "Invalid credentials");
        return "login";
    }
}