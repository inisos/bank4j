package io.inisos.bank4j.validator.constraintvalidators;

import io.inisos.bank4j.BankAccount;
import io.inisos.bank4j.impl.SimpleBankAccountBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IBANValidatorTest {

    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        Locale.setDefault(Locale.ENGLISH);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void isValid_pass_with_valid_IBAN() {

        BankAccount invalidBankAccount = new SimpleBankAccountBuilder().iban("FR7630001007941234567890185").build();

        Set<ConstraintViolation<BankAccount>> constraintViolations =
                validator.validate(invalidBankAccount);

        assertEquals(0, constraintViolations.size());
    }

    @Test
    void isValid_fail_with_invalid_IBAN() {

        BankAccount invalidBankAccount = new SimpleBankAccountBuilder().iban("x").build();

        Set<ConstraintViolation<BankAccount>> constraintViolations =
                validator.validate(invalidBankAccount);

        assertEquals(1, constraintViolations.size());

        assertEquals("must be a valid IBAN", constraintViolations.iterator().next().getMessage());
    }

}
