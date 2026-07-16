package com.app.dto;

import jakarta.validation.constraints.NotBlank;

public class RenameRequest {

    @NotBlank(message = "Bucket name is required")
    private String bucketName;

    @NotBlank(message = "Old name is required")
    private String oldStoredName;

    @NotBlank(message = "New name is required")
    private String newStoredName;

    public RenameRequest() {
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getOldStoredName() {
        return oldStoredName;
    }

    public void setOldStoredName(String oldStoredName) {
        this.oldStoredName = oldStoredName;
    }

    public String getNewStoredName() {
        return newStoredName;
    }

    public void setNewStoredName(String newStoredName) {
        this.newStoredName = newStoredName;
    }
}