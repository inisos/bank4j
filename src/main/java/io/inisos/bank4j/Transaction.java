package io.inisos.bank4j;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Transaction
 */
public interface Transaction {

    /**
     * @return third party identification
     */
    Optional<Party> getParty();

    /**
     * @return cash account
     */
    BankAccount getAccount();

    /**
     * @return transaction amount
     */
    BigDecimal getAmount();

    /**
     * @return transaction currency
     */
    String getCurrencyCode();

    /**
     * @return end-to-end identifier
     */
    String getEndToEndId();

    /**
     * @return optional identifier
     */
    Optional<String> getId();
}
