package io.inisos.bank4j;

/**
 * @author Patrice Blanchardie
 */
public interface BankAccountBuilder {
    BankAccountBuilder iban(String iban);

    BankAccountBuilder bic(String bic);

    BankAccountBuilder name(String name);

    BankAccount build();
}
