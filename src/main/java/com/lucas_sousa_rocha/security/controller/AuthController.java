package com.lucas_sousa_rocha.security.controller;

//import ch.qos.logback.core.model.Model;
import org.springframework.ui.Model;
import com.lucas_sousa_rocha.security.dto.RegisterRequest;
import com.lucas_sousa_rocha.security.model.User;
import com.lucas_sousa_rocha.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;



@Controller
public class AuthController {


    private final UserRepository userRepo;

    private final PasswordEncoder encoder;

    public AuthController(UserRepository userRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }


    @GetMapping("/login")
    public String loginPage() { return "login"; }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new RegisterRequest());
        return "register"; // nome do template: register.html
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute("user") RegisterRequest userForm, Model model) {
        if (userRepo.count() > 0 && userRepo.count() >= 1 /* opcional: permitir apenas 1 registro*/) {
            model.addAttribute("error");
            return "register";
        }
        if (userRepo.findByUsername(userForm.getUsername()).isPresent()) {
            model.addAttribute("error");
            return "register";
        }

        User user = new User();
        user.setUsername(userForm.getUsername());
        user.setPassword(encoder.encode(userForm.getPassword()));
        userRepo.save(user);
        return "redirect:/login?registered";
    }
}
