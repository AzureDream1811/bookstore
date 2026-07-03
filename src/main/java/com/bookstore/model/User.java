package com.bookstore.model;

import java.time.LocalDateTime;

public class User {
    private int userId;
    private String fullName;
    private String email;
    private String passwordHash;
    private String role; // CUSTOMER, STAFF, MANAGER
    private String verifyCode;
    private LocalDateTime otpExpiresAt;
    private boolean verified;

    public User() {}

    public User(int userId, String fullName, String email, String passwordHash, String role) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getVerifyCode() {return verifyCode;}
    public void setVerifyCode(String verifyCode) {this.verifyCode = verifyCode;}
    public LocalDateTime getOtpExpiresAt() {return otpExpiresAt;}
    public void setOtpExpiresAt(LocalDateTime otpExpiresAt) {this.otpExpiresAt = otpExpiresAt;}
    public boolean isVerified() {return verified;}
    public void setVerified(boolean verified) {this.verified = verified;}
}
