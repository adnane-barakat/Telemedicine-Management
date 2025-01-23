package com.clinique.rest.services;

public interface NotificationService {
    String sendNotification(String toEmail, String subject, String message);
}