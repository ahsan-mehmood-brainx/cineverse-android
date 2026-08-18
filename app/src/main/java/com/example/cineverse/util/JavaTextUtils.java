package com.example.cineverse.util;

/**
 * Plain Java utility, called from Kotlin (see Extensions.kt#capitalizeWords) to
 * demonstrate Java/Kotlin interop within the same module.
 */
public final class JavaTextUtils {

    private JavaTextUtils() {
    }

    public static String capitalizeWords(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder(input.length());
        boolean capitalizeNext = true;
        for (char c : input.toCharArray()) {
            if (Character.isWhitespace(c)) {
                capitalizeNext = true;
                result.append(c);
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(Character.toLowerCase(c));
            }
        }
        return result.toString();
    }
}
