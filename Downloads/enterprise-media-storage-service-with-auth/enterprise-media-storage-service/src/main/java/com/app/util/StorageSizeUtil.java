package com.app.util;

public final class StorageSizeUtil {

    private static final String[] UNITS = {"B", "KB", "MB", "GB", "TB", "PB"};

    private StorageSizeUtil() {
    }

    public static String format(long bytes) {
        if (bytes < 0) {
            throw new IllegalArgumentException("Bytes cannot be negative");
        }
        double size = bytes;
        int unitIndex = 0;

        while (size >= 1024 && unitIndex < UNITS.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return String.format("%.2f %s", size, UNITS[unitIndex]);
    }
}