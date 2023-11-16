package io.inisos.bank4j.validator.constraintvalidators;

import io.inisos.bank4j.Bank;
import io.inisos.bank4j.Transaction;
import io.inisos.bank4j.TransactionBuilder;
import io.inisos.bank4j.util.Iso20022ReferenceElementValidator;
import io.inisos.bank4j.validator.constraints.Iso20022CharacterSet;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class Iso20022CharacterSetValidatorTest {
    private static Validator validator;
    private static TransactionBuilder defaultTransaction;

    @BeforeAll
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        defaultTransaction = Bank.simpleTransaction()
                .account(Bank.simpleBankAccount()
                    .iban("FR7630001007941234567890185")
                    .build())
                .amount("200")
                .currency("EUR");
    }

    @Test
    void isValid_pass_with_valid_Iso20022_CharacterSet() {
        Transaction transaction = defaultTransaction.endToEndId("MyEndToEndId").build();

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);

        assertFalse(violations.isEmpty());
    }

    @Test
    void isValid_fail_with_invalid_Iso20022_CharacterSet() {
        Transaction transaction = defaultTransaction.endToEndId("/MyEndToEnd#Id").build();

        Set<ConstraintViolation<Transaction>> violations = 
            validator.validate(transaction);

        assertFalse(violations.isEmpty());
        assertEquals(
            Iso20022CharacterSet.class, 
            violations.iterator().next()
                .getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
        );
    }

    @Test
    void isValid_pass_with_sanitized_invalid_Iso20022_CharacterSet() {
        Transaction transaction = defaultTransaction.endToEndId(
            Iso20022ReferenceElementValidator.sanitizeToCharacterSet("/MyEndToEnd#Id")
        ).build();

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);

        assertFalse(violations.isEmpty());
    }
}
