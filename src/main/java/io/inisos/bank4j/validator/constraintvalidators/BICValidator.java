package io.inisos.bank4j.validator.constraintvalidators;

import de.speedbanking.bic.Bic;
import io.inisos.bank4j.validator.constraints.BIC;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for the {@link BIC} constraint.
 * <p>
 * This validator checks if a given string is a syntactically valid Business Identifier Code (BIC).
 * It supports optional values by returning {@code true} for {@code null} or empty strings.
 */
public class BICValidator implements ConstraintValidator<BIC, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return value == null || value.isEmpty() || Bic.isValid(value);
    }

}
