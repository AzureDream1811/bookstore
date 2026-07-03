package com.bookstore.service;

import com.bookstore.dao.EmailDAO;
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
    private final EmailDAO emailDAO;
    private static final int MAX_ATTEMPTS = 3;

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

        this.emailDAO = new EmailDAO();
    }

    /**
     * Send email individually, retry up to {@value #MAX_ATTEMPTS} times if failed.
     * Each attempt result is saved to database.
     *
     * @param to recipient's email
     * @param subject mail's subject
     * @param body mail's body
     * @throws IllegalArgumentException if {@code to} is not a valid email address
     * @throws MessagingException if all {@value #MAX_ATTEMPTS} attempts fail
     */
    public void sendMail(String to, String subject, String body) throws MessagingException {
        if (!isValidEmail(to)) {
            throw new IllegalArgumentException("invalid email address: " + to);
        }

        MessagingException lastException = null;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
                message.setSubject(subject, "UTF-8");
                message.setText(body, "UTF-8", "html");
                Transport.send(message);
                emailDAO.save(to, subject, "SUCCESS", null, attempt);
                return;

            } catch (MessagingException e) {
                lastException = e;
                System.err.printf("Attempt %d failed: %s%n", attempt, e.getMessage());
            }
        }
        emailDAO.save(to, subject, "FAILED", lastException.getMessage(), MAX_ATTEMPTS);
        throw lastException;
    }

    /**
     * Send mail to many recipients, no retry if failed
     * Each mail failed is saved to database with retryCount = 0
     * Invalid email is skipped, no throw exception, continue to send next mail
     *
     * @param recipients list of recipients
     * @param subject mail's subject
     * @param body mail's body
     * @throws MessagingException if send mail fail
     */
    public void sendBulkMail(List<String> recipients, String subject, String body) throws MessagingException {
        try (Transport transport = session.getTransport("smtp")) {
            transport.connect();
            for (String to : recipients) {
                if (!isValidEmail(to)) {
                    System.out.println("Skipping email address " + to);
                    emailDAO.save(to, subject, "FAILED", "invalid email address", 0);
                    continue;
                }

                try {
                    MimeMessage message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(username));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
                    message.setSubject(subject, "UTF-8");
                    message.setText(body, "UTF-8");
                    transport.sendMessage(message, message.getAllRecipients());
                    emailDAO.save(to, subject, "SUCCESS", null, 0);

                } catch (MessagingException e) {
                    System.err.printf("Failed to send to %s: %s%n", to, e.getMessage());
                    emailDAO.save(to, subject, "FAILED", e.getMessage(), 0);
                }
            }
        }
    }

    private boolean isValidEmail(String email) {
        String pattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email != null && email.matches(pattern);
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
