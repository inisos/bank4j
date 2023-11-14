package io.inisos.bank4j.validator.constraintvalidators;

import io.inisos.bank4j.util.Iso20022ReferenceElementValidator;
import io.inisos.bank4j.validator.constraints.Iso20022CharacterSet;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class Iso20022CharacterSetValidator implements ConstraintValidator<Iso20022CharacterSet, String> {
    private boolean allowDiacritics;

    @Override
    public void initialize(Iso20022CharacterSet constraintAnnotation) {
        this.allowDiacritics = constraintAnnotation.allowDiacritics();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return Iso20022ReferenceElementValidator.isValidCharacterSet(value, this.allowDiacritics);
    }
}
