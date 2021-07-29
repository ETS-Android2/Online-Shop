package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    // Gmail SMTP Server Details
    private static final String HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "465";

    // Gmail Account Credentials
    private static final String GMAIL_ADDRESS = "";
    private static final String GMAIL_PASSWORD = "";

    public Response send(String to, String subject, String body) {

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.host", HOST);
        properties.put("mail.smtp.port", SMTP_PORT);
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        // Get the Session object.// and pass username and password
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication(ADDRESS, PASSWORD);

            }

        });

        // Used to debug SMTP issues
        session.setDebug(true);

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(ADDRESS));

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
        } catch (MessagingException e) {
            return new Response(e.getMessage().toString(), false);
        }

        return new Response("Email is sent!", true);
    }
}
