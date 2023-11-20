package io.inisos.bank4j;

/**
 * @author Patrice Blanchardie
 */
public class TestBankAccountBuilder {

    private String iban;
    private String bic;

    public TestBankAccountBuilder iban(String iban) {
        this.iban = iban;
        return this;
    }

    public TestBankAccountBuilder bic(String bic) {
        this.bic = bic;
        return this;
    }

    public TestBankAccount build() {
        return new TestBankAccount(iban, bic);
    }
}
