package com.example.orda.controller;

import com.example.orda.dto.request.SignupRequest;
import com.example.orda.model.Role;
import com.example.orda.model.User;
import com.example.orda.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashSet;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class WebAuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("signupRequest", new SignupRequest());
        return "signup";
    }

    @PostMapping("/signup")
    public String registerUser(@ModelAttribute SignupRequest signUpRequest, Model model) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            model.addAttribute("error", "Error: Username is already taken!");
            return "signup";
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            model.addAttribute("error", "Error: Email is already in use!");
            return "signup";
        }

        // Create new user's account
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);

        User user = User.builder()
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .password(encoder.encode(signUpRequest.getPassword()))
                .roles(roles)
                .build();

        userRepository.save(user);

        return "redirect:/login?success";
    }
}