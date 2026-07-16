package com.app.dto;

import java.time.Instant;

public class LifecycleResponse {

    private String fileId;

    private String originalName;

    private String status;

    private String message;

    private Instant timestamp;

    public LifecycleResponse() {
    }

    public LifecycleResponse(String fileId, String originalName, String status, String message, Instant timestamp) {
        this.fileId = fileId;
        this.originalName = originalName;
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}