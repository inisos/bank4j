package io.inisos.bank4j;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * @author Patrice Blanchardie
 */
public class TestTransactionBuilder {
    private TestBankAccount account;
    private BigDecimal amount;
    private Currency currency;
    private String endToEndId;
    private String id;

    public TestTransactionBuilder account(TestBankAccount account) {
        this.account = account;
        return this;
    }

    public TestTransactionBuilder amount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public TestTransactionBuilder amount(String amount) {
        return amount(new BigDecimal(amount));
    }

    public TestTransactionBuilder currency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public TestTransactionBuilder currency(String currencyCode) {
        return currency(Currency.getInstance(currencyCode));
    }

    public TestTransactionBuilder endToEndId(String endToEndId) {
        this.endToEndId = endToEndId;
        return this;
    }

    public TestTransactionBuilder id(String id) {
        this.id = id;
        return this;
    }

    public TestTransaction build() {
        return new TestTransaction(account, amount, currency, endToEndId, id);
    }
}
