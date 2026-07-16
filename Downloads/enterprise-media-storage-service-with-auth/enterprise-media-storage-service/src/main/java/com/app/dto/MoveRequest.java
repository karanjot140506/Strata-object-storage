package com.app.dto;

import jakarta.validation.constraints.NotBlank;

public class MoveRequest {

    @NotBlank(message = "Source bucket is required")
    private String sourceBucket;

    @NotBlank(message = "Target bucket is required")
    private String targetBucket;

    @NotBlank(message = "Object key is required")
    private String objectKey;

    public MoveRequest() {
    }

    public String getSourceBucket() {
        return sourceBucket;
    }

    public void setSourceBucket(String sourceBucket) {
        this.sourceBucket = sourceBucket;
    }

    public String getTargetBucket() {
        return targetBucket;
    }

    public void setTargetBucket(String targetBucket) {
        this.targetBucket = targetBucket;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }
}