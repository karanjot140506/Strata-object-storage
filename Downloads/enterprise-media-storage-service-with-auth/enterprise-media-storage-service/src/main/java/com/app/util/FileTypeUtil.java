package com.app.util;

import java.util.Set;

public final class FileTypeUtil {

    private FileTypeUtil() {
    }

    private static final Set<String> ALLOWED_TYPES = Set.of(
            // Images
            "image/png",
            "image/jpeg",
            "image/jpg",
            "image/webp",
            "image/gif",

            // Documents
            "application/pdf",
            "text/plain",
            "text/csv",
            "text/markdown",
            "application/json",
            "application/xml",
            "text/xml",

            // Microsoft Office
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",

            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",

            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",

            // OpenDocument
            "application/vnd.oasis.opendocument.text",
            "application/vnd.oasis.opendocument.spreadsheet",
            "application/vnd.oasis.opendocument.presentation",

            // Archives
            "application/zip",
            "application/x-rar-compressed",
            "application/x-7z-compressed",
            "application/gzip",

            // Media
            "video/mp4",
            "audio/mpeg",
            "audio/wav",

            // APK
            "application/vnd.android.package-archive");

    public static boolean isAllowed(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return false;
        }

        return ALLOWED_TYPES.contains(contentType);
    }

    public static String determineType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return "UNKNOWN";
        }

        // Images
        if (contentType.startsWith("image/")) {
            return "IMAGE";
        }

        // Videos
        if (contentType.startsWith("video/")) {
            return "VIDEO";
        }

        // Audio
        if (contentType.startsWith("audio/")) {
            return "AUDIO";
        }

        // PDF
        if ("application/pdf".equals(contentType)) {
            return "PDF";
        }

        // Word
        if (contentType.equals("application/msword")
                || contentType.equals(
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {

            return "WORD";
        }

        // Excel
        if (contentType.equals("application/vnd.ms-excel")
                || contentType.equals(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {

            return "EXCEL";
        }

        // PowerPoint
        if (contentType.equals("application/vnd.ms-powerpoint")
                || contentType.equals(
                "application/vnd.openxmlformats-officedocument.presentationml.presentation")) {

            return "POWERPOINT";
        }

        // CSV
        if ("text/csv".equals(contentType)) {
            return "CSV";
        }

        // Markdown
        if ("text/markdown".equals(contentType)) {
            return "MARKDOWN";
        }

        // JSON
        if ("application/json".equals(contentType)) {
            return "JSON";
        }

        // XML
        if ("application/xml".equals(contentType)
                || "text/xml".equals(contentType)) {

            return "XML";
        }

        // OpenDocument
        if (contentType.startsWith(
                "application/vnd.oasis.opendocument")) {

            return "OPEN_DOCUMENT";
        }

        // Archives
        if (contentType.equals("application/zip")
                || contentType.equals("application/x-rar-compressed")
                || contentType.equals("application/x-7z-compressed")
                || contentType.equals("application/gzip")) {

            return "ARCHIVE";
        }

        // APK
        if ("application/vnd.android.package-archive".equals(contentType)) {
            return "APK";
        }

        // Source Code
        if (contentType.contains("javascript")
                || contentType.contains("java")
                || contentType.contains("python")
                || contentType.contains("x-python")) {

            return "SOURCE_CODE";
        }

        // Generic Text
        if (contentType.startsWith("text/")) {
            return "TEXT";
        }

        return "DOCUMENT";
    }
}