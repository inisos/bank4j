package io.inisos.bank4j.validator.constraintvalidators;

import io.inisos.bank4j.validator.constraints.BIC;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.iban4j.BicUtil;
import org.iban4j.Iban4jException;

public class BICValidator implements ConstraintValidator<BIC, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value != null && value.length() != 0) {
            try {
                BicUtil.validate(value);
                return true;
            } catch (Iban4jException e) {
                return false;
            }
        } else {
            return true;
        }
    }

}
