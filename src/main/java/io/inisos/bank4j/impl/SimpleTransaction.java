package io.inisos.bank4j.impl;

import io.inisos.bank4j.BankAccount;
import io.inisos.bank4j.Transaction;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Simple Transaction
 *
 * @author Patrice Blanchardie
 */
public class SimpleTransaction implements Transaction {

    private final BankAccount thirdParty;
    private final BigDecimal amount;
    private final Currency currency;
    private final String endToEndId;
    private final String id;

    public SimpleTransaction(BankAccount thirdParty, BigDecimal amount, Currency currency, String endToEndId) {
        this(thirdParty, amount, currency, endToEndId, null);
    }

    public SimpleTransaction(BankAccount thirdParty, BigDecimal amount, String currencyCode, String endToEndId) {
        this(thirdParty, amount, Currency.getInstance(currencyCode), endToEndId, null);
    }

    public SimpleTransaction(BankAccount thirdParty, BigDecimal amount, String currencyCode, String endToEndId, String id) {
        this(thirdParty, amount, Currency.getInstance(currencyCode), endToEndId, id);
    }

    public SimpleTransaction(BankAccount thirdParty, BigDecimal amount, Currency currency, String endToEndId, String id) {
        this.thirdParty = Objects.requireNonNull(thirdParty);
        this.amount = Objects.requireNonNull(amount);
        this.currency = Objects.requireNonNull(currency);
        this.endToEndId = Objects.requireNonNull(endToEndId);
        this.id = id;
    }

    @Override
    public BankAccount getThirdParty() {
        return thirdParty;
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String getCurrencyCode() {
        return currency.getCurrencyCode();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getEndToEndId() {
        return endToEndId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleTransaction)) return false;
        SimpleTransaction that = (SimpleTransaction) o;
        return getThirdParty().equals(that.getThirdParty()) && getAmount().equals(that.getAmount()) && currency.equals(that.currency) && getEndToEndId().equals(that.getEndToEndId()) && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getThirdParty(), getAmount(), currency, getEndToEndId(), getId());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SimpleTransaction.class.getSimpleName() + "[", "]")
                .add("thirdParty=" + thirdParty)
                .add("amount=" + amount)
                .add("currency=" + currency)
                .add("endToEndId='" + endToEndId + "'")
                .add("id='" + id + "'")
                .toString();
    }
}
