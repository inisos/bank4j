package io.inisos.bank4j;

import java.math.BigDecimal;

/**
 * Transaction
 */
public interface Transaction {

    /**
     * @return third party bank account
     */
    BankAccount getThirdParty();

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
     * @return optional identifier (can be null)
     */
    String getId();
}
