package com.lucas_sousa_rocha.security.service;

import com.lucas_sousa_rocha.security.model.User;
import com.lucas_sousa_rocha.security.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    private final EmailService emailService;

    public PasswordResetService(UserRepository userRepository, PasswordEncoder encoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.emailService = emailService;
    }

    public String processResetPasswordService(String token, String password, Model model) {
        Optional<User> userOptional = userRepository.findByPasswordResetToken(token);

        // ✅ Verifica se o usuário existe
        if (userOptional.isEmpty()) {
            model.addAttribute("error", "Token inválido ou expirado");
            return "reset-password";
        }
        User user = userOptional.get();
        // ✅ Verifica se o token está nulo ou expirado
        if (user.getPasswordResetToken() == null || user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Token inválido ou expirado");
            return "reset-password";
        } else {
        // ✅ Atualiza a senha e remove o token
        user.setPassword(encoder.encode(password));
        user.setPasswordResetToken(null);
        user.setTokenExpiry(null);
        userRepository.save(user);
        System.out.println("Usuário " + user.getUsername());
        return "redirect:/login?resetSuccess";
        }
    }


    public String processForgotPasswordService(String email, Model model, HttpServletRequest request) {
        String appUrl = request.getRequestURL().toString().replace(request.getServletPath(), "");
        boolean enviado = emailService.sendPasswordResetEmail(email, appUrl);
        if (enviado) {
            model.addAttribute("mensagem", "Verifique seu e-mail para redefinir a senha.");
            System.out.println("Enviado para redefinir a senha");
        } else {
            System.out.println("Não enviado");
            model.addAttribute("erro", "E-mail não encontrado.");
        }
        return "forgot-password";
    }

}
