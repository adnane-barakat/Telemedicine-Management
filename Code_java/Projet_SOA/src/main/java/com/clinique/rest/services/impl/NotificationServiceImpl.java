package com.clinique.rest.services.impl;


import com.clinique.rest.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;


@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    public String sendNotification(String toEmail, String subject, String message) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(toEmail);
        email.setSubject(subject);
        email.setText(message);
        email.setFrom("notification@telemedecine.ma"); // Adresse de l'expéditeur

        try {
            mailSender.send(email);
            return "Email envoyé avec succès à : " + toEmail;
        } catch (Exception e) {
            return "Erreur lors de l'envoi de l'email : " + e.getMessage();
        }
    }
}