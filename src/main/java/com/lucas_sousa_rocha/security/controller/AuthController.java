package com.lucas_sousa_rocha.security.controller;


import com.lucas_sousa_rocha.security.service.UserService;
import org.springframework.ui.Model;
import com.lucas_sousa_rocha.security.dto.RegisterRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(
            Model model,
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout) {
            if (error != null) {
                model.addAttribute("errorMessage", "Nome de usuário ou senha inválidos.");
            }
            if (logout != null) {
                model.addAttribute("logoutMessage", "Você saiu com sucesso.");
            }
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute("user") RegisterRequest userForm, Model model) {
        return userService.registerUser(userForm, model);
    }

    }
