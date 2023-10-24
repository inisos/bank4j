package io.inisos.bank4j;

import java.util.Optional;

/**
 * Bank Account
 */
public interface BankAccount {

    /**
     * @return IBAN
     */
    String getIban();

    /**
     * @return BIC, can be null
     */
    Optional<String> getBic();

    /**
     * @return name
     */
    Optional<String> getName();
}
