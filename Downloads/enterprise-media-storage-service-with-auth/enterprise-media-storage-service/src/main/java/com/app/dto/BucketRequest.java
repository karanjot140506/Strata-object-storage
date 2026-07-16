package com.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class BucketRequest {

    @NotBlank(message = "Bucket name is required")
    @Size(min = 3, max = 63, message = "Bucket name must be between 3 and 63 characters")
    @Pattern(regexp = "^[a-z0-9.-]+$", message = "Bucket name contains invalid characters")
    private String bucketName;

    public BucketRequest() {
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
}