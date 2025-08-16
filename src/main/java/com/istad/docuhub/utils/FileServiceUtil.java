package com.istad.docuhub.utils;

import java.util.Stack;
import java.util.UUID;
import java.util.regex.Pattern;

public class FileServiceUtil {
    private static final Pattern ILLEGAL_CHARS = Pattern.compile("[^a-zA-Z0-9._-]");

    public static String generateFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.isBlank()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }

        // Extract file extension
        String extension = "";
        String baseName = originalFileName;

        int lastDotIndex = originalFileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            baseName = originalFileName.substring(0, lastDotIndex);
            extension = originalFileName.substring(lastDotIndex); // includes "."
        }

        // Sanitize base name: replace spaces and illegal chars with underscore
        String sanitizedBase = baseName
                .replaceAll("\\s+", "_")           // Replace whitespace (incl. tabs, multiple spaces)
                .replaceAll("[^a-zA-Z0-9._-]", "_"); // Replace any remaining illegal chars

        // Combine UUID + sanitized base + extension
        return UUID.randomUUID() + "_" + sanitizedBase + extension;
    }

    public static  String guessContentType(String fileName) {
        if (fileName.toLowerCase().endsWith(".png")) {
            return "image/png";
        } else if (fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.toLowerCase().endsWith(".gif")) {
            return "image/gif";
        } else if (fileName.toLowerCase().endsWith(".pdf")) {
            return "application/pdf";
        } else if (fileName.toLowerCase().endsWith(".webp")) {
            return "image/webp";
        }
        return null;
    }
}
