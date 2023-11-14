package io.inisos.bank4j.util;

import java.text.Normalizer;

public class StringUtils {
    /**
     * Remove diacritics (accented characters) from a string.
     * This method provides a way to normalize a string by removing diacritics **without any external dependencies**.
     * 
     * @param input the input string containing diacritics
     * @return the input string with diacritics removed
     */
    public static String removeDiacritics(String input) {
        String normalizedString = Normalizer.normalize(input, Normalizer.Form.NFD);
        StringBuilder stringBuilder = new StringBuilder();
        
        for (int i = 0; i < normalizedString.length(); i++) {
            char c = normalizedString.charAt(i);
            if (Character.getType(c) != Character.NON_SPACING_MARK) {
                stringBuilder.append(c);
            }
        }
        
        return stringBuilder.toString();
    }
}
