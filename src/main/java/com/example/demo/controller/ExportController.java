package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.ExportService;
import com.example.demo.service.UserService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.time.LocalDate;

@Controller
@RequestMapping("/export")
public class ExportController {

    private final UserService userService;
    private final ExportService exportService;

    public ExportController(UserService userService, ExportService exportService) {
        this.userService = userService;
        this.exportService = exportService;
    }

    @GetMapping("/activities.csv")
    public ResponseEntity<Resource> activitiesCsv(Principal principal) {
        User user = userService.requireByUsername(principal.getName());
        byte[] data = exportService.activitiesCsv(user);
        String filename = "echotracker-activities-" + LocalDate.now() + ".csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new ByteArrayResource(data));
    }

    @GetMapping("/report.pdf")
    public ResponseEntity<Resource> report(Principal principal) {
        User user = userService.requireByUsername(principal.getName());
        byte[] data = exportService.summaryReport(user);
        String filename = "echotracker-report-" + LocalDate.now() + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new ByteArrayResource(data));
    }
}
