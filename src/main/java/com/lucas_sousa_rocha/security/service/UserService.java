package com.lucas_sousa_rocha.security.service;

import com.lucas_sousa_rocha.security.dto.RegisterRequest;
import com.lucas_sousa_rocha.security.model.User;
import com.lucas_sousa_rocha.security.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;

@Service
public class UserService {

private final UserRepository userRepository;
private final PasswordEncoder encoder;

    public UserService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public String registerUser(RegisterRequest userForm, Model model) {
        if (userRepository.findByUsername(userForm.getUsername()).isPresent()) {
            model.addAttribute("error", "Nome de usuário já está em uso.");
            return "register";
        }
        if (userRepository.findByEmail(userForm.getEmail()).isPresent()) {
            model.addAttribute("error", "E-mail já está cadastrado.");
            return "register";
        }
        User user = new User();
        user.setUsername(userForm.getUsername());
        user.setPassword(encoder.encode(userForm.getPassword()));
        user.setRole("USER");
        user.setEmail(userForm.getEmail());
        user.setInclusion_date(LocalDateTime.now());

        userRepository.save(user);
        return "redirect:/login?registered";
    }

    public String getUser(Principal principal, Model model){
        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
        model.addAttribute("user", user);
        return "my-account";
    }

    public String updateUser(User user, Principal principal, RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        User existingUser = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            redirectAttributes.addFlashAttribute("errorMenssage", "Email já estar em uso!");
            return "redirect:/my-account";
        } else {
            existingUser.setEmail(user.getEmail());
            userRepository.save(existingUser);
            redirectAttributes.addFlashAttribute("successMessage", "Dados atualizados com sucesso!");
            return "redirect:/my-account";
        }
    }
    }

