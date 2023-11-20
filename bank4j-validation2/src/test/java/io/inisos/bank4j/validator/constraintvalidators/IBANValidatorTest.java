package io.inisos.bank4j.validator.constraintvalidators;

import io.inisos.bank4j.TestBankAccount;
import io.inisos.bank4j.TestBankAccountBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IBANValidatorTest {

    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void isValid_pass_with_valid_IBAN() {

        TestBankAccount invalidBankAccount = new TestBankAccountBuilder().iban("FR7630001007941234567890185").build();

        Set<ConstraintViolation<TestBankAccount>> constraintViolations =
                validator.validate(invalidBankAccount);

        assertEquals(0, constraintViolations.size());
    }

    @Test
    void isValid_fail_with_invalid_IBAN() {

        TestBankAccount invalidBankAccount = new TestBankAccountBuilder().iban("x").build();

        Set<ConstraintViolation<TestBankAccount>> constraintViolations =
                validator.validate(invalidBankAccount);

        assertEquals(1, constraintViolations.size());

        assertEquals("must be a valid IBAN", constraintViolations.iterator().next().getMessage());
    }

}
