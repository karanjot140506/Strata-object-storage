package com.app.validator;

import com.app.entity.FileMetadata;
import com.app.entity.FileStatus;
import com.app.exception.ValidationException;

public final class LifecycleValidator {

    private LifecycleValidator() {
    }

    public static void validateArchive(FileMetadata metadata) {

        if (metadata.getStatus() == FileStatus.ARCHIVED) {
            throw new ValidationException("File is already archived.");
        }

        if (metadata.getStatus() == FileStatus.DELETED) {
            throw new ValidationException("Deleted file cannot be archived.");
        }
    }

    public static void validateRestore(FileMetadata metadata) {

        if (metadata.getStatus() == FileStatus.ACTIVE) {
            throw new ValidationException("File is already active.");
        }
    }

    public static void validateSoftDelete(FileMetadata metadata) {

        if (metadata.getStatus() == FileStatus.DELETED) {
            throw new ValidationException("File is already deleted.");
        }
    }

    public static void validatePermanentDelete(FileMetadata metadata) {

        if (metadata.getStatus() != FileStatus.DELETED) {
            throw new ValidationException(
                    "Only soft deleted files can be permanently deleted.");
        }
    }
}