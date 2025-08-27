package com.istad.docuhub.slugGeneration;

import java.text.Normalizer;

public class SlugUtil {
    public static String toSlug(String input) {
        // 1. Normalize (remove accents like é → e)
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        // 2. Keep only letters, numbers, and spaces
        String clean = normalized.replaceAll("[^a-zA-Z0-9\\s]", "");
        // 3. Replace spaces with "-" and lowercase it
        return clean.trim().replaceAll("\\s+", "-").toLowerCase();
    }
    public static String toSlug(String input, String input2) {
        // 1. Normalize (remove accents like é → e)
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        // 2. Keep only letters, numbers, and spaces
        String clean = normalized.replaceAll("[^a-zA-Z0-9\\s]", "");
        // 3. Replace spaces with "-" and lowercase it
        return clean.trim().replaceAll("\\s+", "-").toLowerCase();
    }

}
