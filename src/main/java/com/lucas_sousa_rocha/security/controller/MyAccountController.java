package com.lucas_sousa_rocha.security.controller;

import com.lucas_sousa_rocha.security.model.User;
import com.lucas_sousa_rocha.security.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class MyAccountController {

    private final UserRepository userRepository;

    public MyAccountController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/my-account")
    public String myAccount(Model model, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        model.addAttribute("user", user);
        return "my-account";
    }

    // Adicionar este metodo para processar a atualização dos dados
    @PostMapping("/update-account")
    public String updateAccount(@ModelAttribute("user") User updatedUser,Principal principal,RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        User existingUser = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
        if (userRepository.findByEmail(updatedUser.getEmail()).isPresent()) {
            redirectAttributes.addFlashAttribute("errorMenssage", "Email já estar em uso!");
            return "redirect:/my-account";
        } else {
            existingUser.setEmail(updatedUser.getEmail());
            userRepository.save(existingUser);
            redirectAttributes.addFlashAttribute("successMessage", "Dados atualizados com sucesso!");
            return "redirect:/my-account";
        }
    }

}
