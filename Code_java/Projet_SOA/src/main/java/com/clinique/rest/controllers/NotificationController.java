package com.clinique.rest.controllers;

import com.clinique.rest.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService emailService;

    @PostMapping("/email")
    public String sendEmail(
            @RequestParam String toEmail,
            @RequestParam String subject,
            @RequestParam String message) {
        emailService.sendNotification(toEmail, subject, message);
        return "Email envoyé avec succès à " + toEmail;
    }

}