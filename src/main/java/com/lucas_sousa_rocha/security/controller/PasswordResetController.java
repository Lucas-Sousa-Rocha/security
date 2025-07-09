package com.lucas_sousa_rocha.security.controller;

import com.lucas_sousa_rocha.security.dto.RegisterRequest;
import com.lucas_sousa_rocha.security.model.User;
import com.lucas_sousa_rocha.security.repository.UserRepository;
import com.lucas_sousa_rocha.security.service.PasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class PasswordResetController {

    private final UserRepository userRepository;

    private final PasswordResetService passwordResetService;

    private final PasswordEncoder encoder;

    public PasswordResetController(UserRepository userRepository, PasswordResetService passwordResetService, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.passwordResetService = passwordResetService;
        this.encoder = encoder;
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, HttpServletRequest request, Model model) {
        boolean enviado = passwordResetService.sendPasswordResetEmail(
                email, request.getRequestURL().toString().replace(request.getServletPath(), "")
        );

        if (enviado) {
            model.addAttribute("mensagem", "Verifique seu e-mail para redefinir a senha.");
            System.out.println("Enviado para redefinir a senha");
        } else {
            System.out.println("Não enviado");
            model.addAttribute("erro", "E-mail não encontrado.");
        }

        return "forgot-password"; // página HTML com form para digitar endereço digital
    }

    @GetMapping("/reset-password")
    public String showResetForm(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token, @RequestParam("password") String password,Model model, RegisterRequest userForm) {
        Optional<User> userOptional = userRepository.findByPasswordResetToken(token);
        if (userOptional.isEmpty() || userOptional.get().getTokenExpiry().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Token inválido ou expirado");
            return "reset-password";}
        else {
        User user = userOptional.get();
        user.setPassword(encoder.encode(userForm.getPassword()));
        //user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setPasswordResetToken(null);
        user.setTokenExpiry(null);
        userRepository.save(user);
        System.out.println("Usuário " + user.getUsername());
        return "redirect:/login?resetSuccess";}
    }

}

