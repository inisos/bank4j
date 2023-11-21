package io.inisos.bank4j.validator.constraintvalidators;

import io.inisos.bank4j.Bank;
import io.inisos.bank4j.Transaction;
import io.inisos.bank4j.TransactionBuilder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

class TransactionValidatorTest {
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
            .currency("EUR")
            .endToEndId("MyEndToEndId");
    }

    @Test
    public void isValid_pass_with_valid_remittance_information() {
        Transaction transaction = defaultTransaction
            .remittanceInformationUnstructured(Collections.singleton("My unstructured remittance information"))
            .build();

            Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);

            Assertions.assertTrue(violations.isEmpty());
    }

    @Test
    public void isValid_fail_with_invalid_remittance_information() {
        Transaction transaction = defaultTransaction
            .remittanceInformationUnstructured(Collections.singleton("kxwzbfbkuxjvpgwdrmbvjzgyjtgewfuopbseeuvzobmxhyiuzyxxksoqitapigozpeeoidjjxzfpixzybktauxsllyfcxiwucomrogwpewlevyflervetnxhjacuwnpbxlqlwcxdswsrf"))
            .build();

            Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);

            Assertions.assertTrue(violations.size() > 0);
    }
}
