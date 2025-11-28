package com.example.perkmanager.event;

import java.time.LocalDateTime;

/**
 * Domain Event: Published when a new user registers
 */
public class UserRegisteredEvent {
    private Long userId;
    private String email;
    private LocalDateTime timestamp;

    public UserRegisteredEvent() {}

    public UserRegisteredEvent(Long userId, String email, LocalDateTime timestamp) {
        this.userId = userId;
        this.email = email;
        this.timestamp = timestamp;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
