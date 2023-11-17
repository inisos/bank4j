package io.inisos.bank4j.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Iso20022ReferenceElementValidator {
    private static final Pattern ISO20022_VALID_CHARS_PATTERN = Pattern.compile("^[A-Za-z0-9\\(\\)\\+\\,\\-\\./:\\s]+$");
    private static final Pattern ISO20022_INVALID_START_OR_END_PATTERN = Pattern.compile("^[/\\s]|[//\\s]$");
    private static final String ISO20022_INVALID_POSITION = "//";
    private static final Pattern ISO20022_INVALID_POSITION_PATTERN = Pattern.compile(ISO20022_INVALID_POSITION);
    private static final char DEFAULT_INVALID_CHAR_REPLACEMENT = '.';

    private static final Map<Character, Character> DEFAULT_REPLACEMENTS;

    // default mapping remplacements
    static {
        DEFAULT_REPLACEMENTS = new HashMap<>();
        DEFAULT_REPLACEMENTS.put('/', '-');
    }

    /**
     * Checks if a given input string is valid according to ISO20022 reference elements character set rules.
     *
     * @param input String to be checked for validity.
     * @return true if the input string is valid according to ISO20022, false otherwise.
     */
    public static boolean isValidCharacterSet(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        return ISO20022_VALID_CHARS_PATTERN.matcher(input).find()
            && !ISO20022_INVALID_START_OR_END_PATTERN.matcher(input).find()
            && !ISO20022_INVALID_POSITION_PATTERN.matcher(input).find();
    }

    public static String sanitizeToCharacterSet(String input) {
        return sanitizeToCharacterSet(input, DEFAULT_REPLACEMENTS);
    }

    /**
     * Sanitizes a given string to conform to the ISO20022 character set standards for reference elements.
     * 1. Any characters in the input string that do not match the ISO20022 valid characters set are replaced by a character specified in the replacements map. 
     * 2. If no replacements are provided for a specific character, the default replacement character "DEFAULT_INVALID_CHAR_REPLACEMENT" is used. 
     * 4. Then, the method ensures that string does not start or end with an invalid character ("/" or " ") 
     * by removing them, and remove any "//" sequence aswell.
     *
     * @param input String to be sanitized.
     * @param replacements Custom map of characters to be replaced and their replacements.
     * @return The sanitized string.
     */
    public static String sanitizeToCharacterSet(String input, Map<Character, Character> replacements) {
        StringBuilder sanitizedInput = new StringBuilder();
        char[] chars = input.toCharArray();

        for (char c : chars) {
            sanitizedInput.append(
                ISO20022_VALID_CHARS_PATTERN.matcher(String.valueOf(c)).find()
                    ? c
                    : replacements.getOrDefault(c, DEFAULT_INVALID_CHAR_REPLACEMENT).charValue()
            );
        }

        String result = sanitizedInput.toString();

        return ISO20022_INVALID_START_OR_END_PATTERN.matcher(result)
            .replaceAll("")
            .replace(ISO20022_INVALID_POSITION, "");
    }
}
