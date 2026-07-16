package com.app.validator;

import com.app.exception.ValidationException;
import com.app.util.FileTypeUtil;
import org.springframework.web.multipart.MultipartFile;

public final class UploadValidator {

    private static final long MAX_FILE_SIZE = 100L * 1024 * 1024;

    private UploadValidator() {
    }

    public static void validate(MultipartFile file, String bucketName, String uploadedBy) {

        if (file == null || file.isEmpty()) {
            throw new ValidationException("File cannot be empty");
        }

        if (!FileTypeUtil.isAllowed(file.getContentType())) {
            throw new ValidationException("Unsupported file type: " + file.getContentType());
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null || fileName.isBlank()) {
            throw new ValidationException("Invalid file name");
        }

        if (fileName.contains("..")) {
            throw new ValidationException("Invalid file name");
        }

        if (file.getContentType() == null) {
            throw new ValidationException("Content type missing");
        }

        if (bucketName == null || bucketName.trim().isEmpty()) {
            throw new ValidationException("Bucket name required");
        }

        if (uploadedBy == null || uploadedBy.trim().isEmpty()) {
            throw new ValidationException("UploadedBy required");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ValidationException("File size exceeds 100 MB limit");
        }
    }
}