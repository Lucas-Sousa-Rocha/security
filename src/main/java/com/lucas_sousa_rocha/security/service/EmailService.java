package com.lucas_sousa_rocha.security.service;

import com.lucas_sousa_rocha.security.model.ConfigEmail;
import com.lucas_sousa_rocha.security.model.User;
import com.lucas_sousa_rocha.security.repository.ConfigEmailRepository;
import com.lucas_sousa_rocha.security.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

@Service
public class EmailService {

    private final UserRepository userRepository;
    private final ConfigEmailRepository configEmailRepository;

    public EmailService(UserRepository userRepository, ConfigEmailRepository configEmailRepository) {
        this.userRepository = userRepository;
        this.configEmailRepository = configEmailRepository;
    }

    public boolean sendPasswordResetEmail(String email, String appUrl) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return false;
        }

        User user = optionalUser.get();

        // Gera token
        String token = UUID.randomUUID().toString();
        user.setPasswordResetToken(token);
        user.setTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        // Busca configuração de e-mail no banco
        ConfigEmail config = configEmailRepository.findById(1L) // Aqui você pode buscar pelo ID fixo ou último cadastrado
                .orElseThrow(() -> new RuntimeException("Configuração de e-mail não encontrada"));

        // Configura SMTP dinamicamente
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(config.getSmtpHost());
        mailSender.setPort(config.getSmtpPort());
        mailSender.setUsername(config.getEmail());
        mailSender.setPassword(config.getSenha());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", String.valueOf(config.isTls()));

        // Monta mensagem
        String resetUrl = appUrl + "/reset-password?token=" + token;
        String message = "Olá, " + user.getUsername() + "!\n\n" +
                "Clique no link abaixo para redefinir sua senha. Ele expirará em 1 hora:\n\n" +
                resetUrl + "\n\nSe você não solicitou, ignore este e-mail.";

        SimpleMailMessage emailMessage = new SimpleMailMessage();
        emailMessage.setTo(email);
        emailMessage.setSubject("Redefinição de Senha");
        emailMessage.setText(message);
        emailMessage.setFrom(config.getEmail());

        // Envia
        mailSender.send(emailMessage);

        return true;
    }

    public boolean sendTestEmail(String to) {
        ConfigEmail config = configEmailRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Configuração de e-mail não encontrada"));

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(config.getSmtpHost());
        mailSender.setPort(config.getSmtpPort());
        mailSender.setUsername(config.getEmail());
        mailSender.setPassword(config.getSenha());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", String.valueOf(config.isTls()));

        SimpleMailMessage emailMessage = new SimpleMailMessage();
        emailMessage.setTo(to);
        emailMessage.setSubject("E-mail de Teste");
        emailMessage.setText("Este é um teste de configuração de e-mail.");
        emailMessage.setFrom(config.getEmail());

        mailSender.send(emailMessage);

        return true;
    }


}
