package com.lucas_sousa_rocha.security.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import com.lucas_sousa_rocha.security.dto.UserFilterDTO;
import com.lucas_sousa_rocha.security.model.User;
import com.lucas_sousa_rocha.security.repository.UserRepository;
import com.lucas_sousa_rocha.security.specification.UserSpecifications;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportService {

    private final UserRepository userRepository;

    @Autowired
    public ReportService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Busca usuários de acordo com os filtros informados.
     * Normaliza strings (trim e converte "" para null).
     */
    public List<User> findUsersByFilters(UserFilterDTO filterDTO) {
        String username = normalize(filterDTO.getUsername());
        String email = normalize(filterDTO.getEmail());
        String role = normalize(filterDTO.getRole());

        LocalDateTime startDateTime = (filterDTO.getStartDate() != null)
                ? filterDTO.getStartDate().atStartOfDay()
                : null;

        LocalDateTime endDateTime = (filterDTO.getEndDate() != null)
                ? filterDTO.getEndDate().atTime(LocalTime.MAX)
                : null;

        Specification<User> spec = Specification
                .where(UserSpecifications.usernameContains(username))
                .and(UserSpecifications.emailContains(email))
                .and(UserSpecifications.hasRole(role))
                .and(UserSpecifications.dateAfter(startDateTime))
                .and(UserSpecifications.dateBefore(endDateTime));

        return userRepository.findAll(spec);
    }

    /**
     * Gera relatório em PDF com a lista de usuários.
     * Lança DocumentException e IOException em caso de erro na geração.
     */
    public void generatePdfReport(List<User> users, HttpServletResponse response) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        addReportTitle(document);
        addReportDate(document);

        PdfPTable table = createTable();
        fillTableData(table, users);

        document.add(table);

        addFooter(document, users.size());

        document.close();
    }

    // Normaliza string: trim e converte "" para null
    private String normalize(String value) {
        return (value == null || value.trim().isEmpty()) ? null : value.trim();
    }

    private void addReportTitle(Document document) throws DocumentException {
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Relatório de Usuários", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
    }

    private void addReportDate(Document document) throws DocumentException {
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Paragraph dateInfo = new Paragraph(
                "Relatório gerado em: " +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                normalFont
        );
        dateInfo.setAlignment(Element.ALIGN_RIGHT);
        dateInfo.setSpacingAfter(20);
        document.add(dateInfo);
    }

    private PdfPTable createTable() throws DocumentException {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setWidths(new float[]{1f, 3f, 4f, 2f, 3f});

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        addTableHeader(table, headerFont, "ID", "Usuário", "Email", "Perfil", "Data de Cadastro");

        return table;
    }

    private void fillTableData(PdfPTable table, List<User> users) {
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (User user : users) {
            addTableCell(table, dataFont, String.valueOf(user.getId()));
            addTableCell(table, dataFont, user.getUsername());
            addTableCell(table, dataFont, user.getEmail());
            addTableCell(table, dataFont, user.getRole());
            addTableCell(table, dataFont, user.getInclusion_date() != null
                    ? user.getInclusion_date().format(formatter)
                    : "");
        }
    }

    private void addFooter(Document document, int total) throws DocumentException {
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Paragraph footer = new Paragraph("Total de usuários: " + total, normalFont);
        footer.setSpacingBefore(20);
        document.add(footer);
    }

    private void addTableHeader(PdfPTable table, Font font, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, font));
            cell.setBackgroundColor(new Color(211, 211, 211)); // cinza claro
            cell.setPadding(5);
            table.addCell(cell);
        }
    }

    private void addTableCell(PdfPTable table, Font font, String value) {
        PdfPCell cell = new PdfPCell(new Phrase(value != null ? value : "", font));
        cell.setPadding(5);
        table.addCell(cell);
    }
}
