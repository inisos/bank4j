package io.inisos.bank4j;

import io.inisos.bank4j.validator.constraints.BIC;
import io.inisos.bank4j.validator.constraints.IBAN;

import java.util.Optional;

/**
 * Test Bank Account with IBAN and BIC validation
 *
 * @author Patrice Blanchardie
 */
public class TestBankAccount {

    @IBAN
    private final String iban;

    @BIC
    private final String bic;

    public TestBankAccount(String iban, String bic) {
        this.iban = iban;
        this.bic = bic;
    }

    public Optional<String> getIban() {
        return Optional.ofNullable(iban);
    }

    public Optional<String> getBic() {
        return Optional.ofNullable(bic);
    }

}
