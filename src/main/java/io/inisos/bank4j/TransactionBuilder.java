package io.inisos.bank4j;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

public interface TransactionBuilder {

    TransactionBuilder party(Party party);

    TransactionBuilder account(BankAccount account);

    TransactionBuilder amount(BigDecimal amount);

    /**
     * Shortcut for {@link #amount(BigDecimal)}
     *
     * @param amount amount as a string
     * @return this
     */
    default TransactionBuilder amount(String amount) {
        return amount(new BigDecimal(amount));
    }

    TransactionBuilder currency(Currency currency);

    /**
     * Shortcut for {@link #currency(Currency)}
     *
     * @param currencyCode currency code
     * @return this
     */
    default TransactionBuilder currency(String currencyCode) {
        return currency(Currency.getInstance(currencyCode));
    }

    TransactionBuilder endToEndId(String endToEndId);

    TransactionBuilder id(String id);

    TransactionBuilder intermediaryAgents(List<BankAccount> intermediaryAgents);

    TransactionBuilder intermediaryAgent(BankAccount intermediaryAgent);

    Transaction build();
}
