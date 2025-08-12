package com.lucas_sousa_rocha.security.controller;

import com.lucas_sousa_rocha.security.dto.UserFilterDTO;
import com.lucas_sousa_rocha.security.model.User;
import com.lucas_sousa_rocha.security.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String showReportForm(Model model) {
        model.addAttribute("userFilter", new UserFilterDTO());
        return "user-report-filter";
    }

    @PostMapping("/users/search")
    @PreAuthorize("hasRole('ADMIN')")
    public String searchUsers(@ModelAttribute UserFilterDTO filterDTO, Model model) {
        List<User> users = reportService.findUsersByFilters(filterDTO);
        model.addAttribute("users", users);
        model.addAttribute("userFilter", filterDTO);
        return "user-report-filter";
    }

    @GetMapping("/users/generate-pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public void generatePdf(
            @ModelAttribute UserFilterDTO filterDTO,
            HttpServletResponse response) throws IOException {

        List<User> users = reportService.findUsersByFilters(filterDTO);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=relatorio-usuarios.pdf");

        reportService.generatePdfReport(users, response);
    }
}

