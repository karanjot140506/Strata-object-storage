package com.app.entity;

public enum FileStatus {

    ACTIVE,
    ARCHIVED,
    DELETED;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isArchived() {
        return this == ARCHIVED;
    }

    public boolean isDeleted() {
        return this == DELETED;
    }
}