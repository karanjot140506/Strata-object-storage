package com.app.dto;

import java.time.Instant;

public class StatusUpdateResponse {
    private String fileId;
    private String oldStatus;
    private String newStatus;
    private Instant updatedAt;

    public StatusUpdateResponse() {
    }

    public StatusUpdateResponse(String fileId, String oldStatus, String newStatus, Instant updatedAt) {
        this.fileId = fileId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.updatedAt = updatedAt;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}