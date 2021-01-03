package io.inisos.bank4j;

/**
 * Bank Account
 */
public interface BankAccount {

    /**
     * @return name
     */
    String getName();

    /**
     * @return IBAN
     */
    String getIban();

    /**
     * @return BIC, can be null
     */
    String getBic();
}
