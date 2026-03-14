package com.Shubham.devconnect.service;


import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("DevConnect — Email Verification");
        message.setText(
                "Welcome to DevConnect! 🚀\n\n" +
                        "Your OTP for email verification is:\n\n" +
                        "OTP: " + otp + "\n\n" +
                        "This OTP is valid for 10 minutes.\n\n" +
                        "If you didn't register on DevConnect, ignore this email.\n\n" +
                        "Team DevConnect"
        );
        mailSender.send(message);
    }
}