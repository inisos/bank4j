package io.inisos.bank4j;

import java.util.Optional;

/**
 * Bank Account
 */
public interface BankAccount {

    /**
     * @return IBAN
     */
    Optional<String> getIban();

    /**
     * @return BIC
     */
    Optional<String> getBic();

    /**
     * @return name
     */
    Optional<String> getName();

    /**
     * @return other account identifier
     */
    Optional<String> getOtherId();
}
