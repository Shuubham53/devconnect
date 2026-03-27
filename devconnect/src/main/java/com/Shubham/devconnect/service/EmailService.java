package com.Shubham.devconnect.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${brevo.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendOtpEmail(String toEmail, String otp) {

        String url = "https://api.brevo.com/v3/smtp/email";

        Map<String, Object> body = new HashMap<>();

        // Sender
        Map<String, String> sender = new HashMap<>();
        sender.put("email", "shubhamnishad110@gmail.com");

        // Receiver
        List<Map<String, String>> toList = new ArrayList<>();
        Map<String, String> to = new HashMap<>();
        to.put("email", toEmail);
        toList.add(to);

        // Email content
        body.put("sender", sender);
        body.put("to", toList);
        body.put("subject", "DevConnect — Email Verification");
        body.put("textContent", "Your OTP is: " + otp);

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("api-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Request
        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response =
                    restTemplate.postForEntity(url, request, String.class);

            System.out.println("✅ Email sent via API: " + response.getStatusCode());

        } catch (Exception e) {
            System.out.println("❌ API Error: " + e.getMessage());
        }
    }
}