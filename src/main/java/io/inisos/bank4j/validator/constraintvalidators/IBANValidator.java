package io.inisos.bank4j.validator.constraintvalidators;

import io.inisos.bank4j.validator.constraints.IBAN;
import org.iban4j.Iban4jException;
import org.iban4j.IbanUtil;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IBANValidator implements ConstraintValidator<IBAN, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value != null && value.length() != 0) {
            try {
                IbanUtil.validate(value);
                return true;
            } catch (Iban4jException e) {
                return false;
            }
        } else {
            return true;
        }
    }

}
