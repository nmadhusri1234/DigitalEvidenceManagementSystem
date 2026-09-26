package com.model;

import java.time.LocalDateTime;

public class AuditLog {

    private int logId;
    private User user;
    private String action;
    private String description;
    private LocalDateTime timestamp;

    public AuditLog(int logId, User user, String action,
                    String description, LocalDateTime timestamp) {

        this.logId = logId;
        this.user = user;
        this.action = action;
        this.description = description;
        this.timestamp = timestamp;
    }

    public int getLogId() {
        return logId;
    }

    public User getUser() {
        return user;
    }

    public String getAction() {
        return action;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {

        return "AuditLog{" +
                "logId=" + logId +
                ", user=" + user.getUserName() +
                ", action='" + action + '\'' +
                ", description='" + description + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}