package com.lucas_sousa_rocha.security.service;

import com.lucas_sousa_rocha.security.model.User;
import com.lucas_sousa_rocha.security.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;

    private final JavaMailSender mailSender;


    public PasswordResetService(UserRepository userRepository, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    public boolean sendPasswordResetEmail(String email, String appUrl) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Gera um token único e define o prazo de expiração
            String token = UUID.randomUUID().toString();
            user.setPasswordResetToken(token);
            user.setTokenExpiry(LocalDateTime.now().plusHours(1));
            userRepository.save(user);

            // Constrói o link para redefinição de senha
            String resetUrl = appUrl + "/reset-password?token=" + token;
            String message = "Olá, " + user.getUsername() + "!\n\n" +
                    "Clique no link abaixo para redefinir sua senha. Ele expirará em 1 hora:\n\n" +
                    resetUrl + "\n\nSe você não solicitou, ignore este e-mail.";

            // Monta o e-mail
            SimpleMailMessage emailMessage = new SimpleMailMessage();
            emailMessage.setTo(email);
            emailMessage.setSubject("Redefinição de Senha");
            emailMessage.setText(message);
            emailMessage.setFrom("kukutix2@gmail.com"); // ✅ adicione explicitamente

            // Envia o e-mail
            mailSender.send(emailMessage);

            return true;
        }

        return false; // Se não encontrou o e-mail
    }
}
