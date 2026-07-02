package com.bookstore.service;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class EmailService {
    private final String username;
    private final Session session;

    public EmailService() {

        Properties properties = new Properties();
        try (InputStream in = EmailService.class
                .getClassLoader()
                .getResourceAsStream("mail.properties")) {

            if (in == null) {
                throw new IOException("email.properties not found");
            }

            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load email.properties", e);
        }

        Dotenv dotenv = Dotenv.load();
        this.username = dotenv.get("MAIL_USERNAME");
        String password = dotenv.get("MAIL_PASSWORD");

        if (username == null || password == null) {
            throw new IllegalStateException("MAIL_USERNAME or MAIL_PASSWORD not set");
        }


        this.session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    public void sendMail(String to, String subject, String body) throws MessagingException {
        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("recipients cannot be null or blank");
        }

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject, "UTF-8");
        message.setText(body, "UTF-8", "html");
        Transport.send(message);
    }

    public void sendBulkMail(List<String> recipients, String subject, String body) throws MessagingException {
        try (Transport transport = session.getTransport("smtp")) {
            transport.connect();
            for (String to : recipients) {
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
                message.setSubject(subject, "UTF-8");
                message.setText(body, "UTF-8");
                transport.sendMessage(message, message.getAllRecipients());
            }
        }
    }

    public static void main(String[] args) {
        EmailService emailService = new EmailService();

        try {
            emailService.sendMail(
                    "newspeedster2099@gmail.com",
                    "Test Subject",
                    "<h1>Test HTML</h1>"
            );
            System.out.println("sendMail OK");
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }

        try {
            emailService.sendBulkMail(
                    List.of("newspeedster2099@gmail.com", "23130068@st.hcmuaf.edu.vn"),
                    "Bulk Subject",
                    "Bulk body"
            );
            System.out.println("sendBulkMail OK");
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
