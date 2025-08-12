package com.lucas_sousa_rocha.security.controller;

import com.lucas_sousa_rocha.security.model.ConfigEmail;
import com.lucas_sousa_rocha.security.repository.ConfigEmailRepository;
import com.lucas_sousa_rocha.security.service.EmailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/config-email")
public class ConfigEmailController {

    private final ConfigEmailRepository configRepo;
    private final EmailService emailService;

    public ConfigEmailController(ConfigEmailRepository configRepo, EmailService emailService) {
        this.configRepo = configRepo;
        this.emailService = emailService;
    }

    @GetMapping
    public String paginaConfig(Model model) {
        ConfigEmail config = configRepo.findById(1L).orElse(new ConfigEmail());
        model.addAttribute("config", config);
        return "config-email";
    }

    @PostMapping("/salvar")
    public String salvarConfig(ConfigEmail configForm) {
        try {
            ConfigEmail configExistente = configRepo.findById(1L).orElse(new ConfigEmail());
            configExistente.setSmtpHost(configForm.getSmtpHost());
            configExistente.setSmtpPort(configForm.getSmtpPort());
            configExistente.setEmail(configForm.getEmail());
            configExistente.setSenha(configForm.getSenha());
            configExistente.setTls(configForm.isTls());
            configRepo.save(configExistente);
            return "redirect:/config-email?sucesso";
        } catch (Exception e) {
            String erro = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/config-email?erro=" + erro;
        }
    }

    @PostMapping("/teste")
    public String enviarTeste(Model model) {
        try {
            boolean enviado = emailService.sendTestEmail("destinatario@teste.com");
            if (enviado) {
                return "redirect:/config-email?sucesso";
            } else {
                String erro = URLEncoder.encode("Falha ao enviar o e-mail de teste", StandardCharsets.UTF_8);
                return "redirect:/config-email?erro=" + erro;
            }
        } catch (Exception e) {
            String erro = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/config-email?erro=" + erro;
        }
    }
}
