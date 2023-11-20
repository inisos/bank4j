package io.inisos.bank4j;

import io.inisos.bank4j.validator.constraints.Iso20022CharacterSet;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;
import java.util.Optional;

/**
 * Simple Transaction
 *
 * @author Patrice Blanchardie
 */
public class TestTransaction {
    private final TestBankAccount account;
    private final BigDecimal amount;
    private final Currency currency;
    @Iso20022CharacterSet
    private final String endToEndId;
    @Iso20022CharacterSet
    private final String id;

    public TestTransaction(TestBankAccount account, BigDecimal amount, Currency currency, String endToEndId, String id) {
        this.account = Objects.requireNonNull(account, "Account cannot be null");
        this.amount = Objects.requireNonNull(amount, "Amount cannot be null");
        this.currency = Objects.requireNonNull(currency, "Currency cannot be null");
        this.endToEndId = Objects.requireNonNull(endToEndId, "End to end id cannot be null");
        this.id = id;
    }

    public TestBankAccount getAccount() {
        return account;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrencyCode() {
        return currency.getCurrencyCode();
    }

    public String getEndToEndId() {
        return endToEndId;
    }

    public Optional<String> getId() {
        return Optional.ofNullable(id);
    }

}
