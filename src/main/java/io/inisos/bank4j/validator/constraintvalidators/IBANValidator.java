package io.inisos.bank4j.validator.constraintvalidators;

import de.speedbanking.iban.Iban;
import io.inisos.bank4j.validator.constraints.IBAN;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for the {@link IBAN} constraint.
 * <p>
 * This validator checks if a given string is a syntactically valid International Bank Account Number (IBAN).
 * It supports optional values by returning {@code true} for {@code null} or empty strings.
 */
public class IBANValidator implements ConstraintValidator<IBAN, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return value == null || value.isEmpty() || Iban.isValid(value);
    }

}
