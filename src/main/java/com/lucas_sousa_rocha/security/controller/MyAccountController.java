package com.lucas_sousa_rocha.security.controller;

import com.lucas_sousa_rocha.security.model.User;
import com.lucas_sousa_rocha.security.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class MyAccountController {

    private final UserService userService;

    public MyAccountController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/my-account")
    public String myAccount(Model model, Principal principal) {
        return userService.getUser(principal, model);
    }

    // Adicionar este método para processar a atualização dos dados
    @PostMapping("/update-account")
    public String updateAccount(@ModelAttribute("user") User updatedUser,Principal principal,RedirectAttributes redirectAttributes) {
        return userService.updateUser(updatedUser, principal, redirectAttributes);
    }

}
