package io.inisos.bank4j;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.inisos.bank4j.util.Iso20022ReferenceElementValidator;

public class Iso20022ReferenceElementValidatorTest {
    
    @Test
    public void test_characterset_with_valid_input() {
        String validInput = "ABCDEF1234";
        Assertions.assertTrue(Iso20022ReferenceElementValidator.isValidCharacterSet(validInput));
    }
    
    @Test
    public void test_characterset_with_invalid_input() {
        String invalidInput = "/ABC#DEF1234";
        Assertions.assertFalse(Iso20022ReferenceElementValidator.isValidCharacterSet(invalidInput));
    }
    
    @Test
    public void test_characterset_with_invalid_input_sanitized() {
        String validInput = "AB#CDE F\\123";
        String expectedOutput = "AB.CDE F.123";
        Assertions.assertEquals(expectedOutput, Iso20022ReferenceElementValidator.sanitizeToCharacterSet(validInput));
    }
    
    @Test
    public void test_characterset_with_invalid_input_sanitized_with_custom_replacements() {
        String input = "/He##ø\\Wørld!/";

        Map<Character, Character> customReplacements = new HashMap<>();
        customReplacements.put('#', 'l');
        customReplacements.put('ø', 'o');
        customReplacements.put('l', 'i');
        customReplacements.put('!', '?');
        customReplacements.put('\\', ' ');

        Assertions.assertEquals("Hello World?", Iso20022ReferenceElementValidator.sanitizeToCharacterSet(input, customReplacements));
    }
}
