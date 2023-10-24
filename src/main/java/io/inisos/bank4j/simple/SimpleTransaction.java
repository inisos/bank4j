package io.inisos.bank4j.simple;

import io.inisos.bank4j.BankAccount;
import io.inisos.bank4j.Party;
import io.inisos.bank4j.Transaction;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * Simple Transaction
 *
 * @author Patrice Blanchardie
 */
public class SimpleTransaction implements Transaction {

    private final Party party;
    private final BankAccount account;
    private final BigDecimal amount;
    private final Currency currency;
    private final String endToEndId;
    private final String id;

    public SimpleTransaction(Party party, BankAccount account, BigDecimal amount, Currency currency, String endToEndId, String id) {
        this.party = party;
        this.account = Objects.requireNonNull(account, "Account cannot be null");
        this.amount = Objects.requireNonNull(amount, "Amount cannot be null");
        this.currency = Objects.requireNonNull(currency, "Currency cannot be null");
        this.endToEndId = Objects.requireNonNull(endToEndId, "End to end id cannot be null");
        this.id = id;
    }

    @Override
    public Optional<Party> getParty() {
        return Optional.ofNullable(party);
    }

    @Override
    public BankAccount getAccount() {
        return account;
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
    public String getEndToEndId() {
        return endToEndId;
    }

    @Override
    public Optional<String> getId() {
        return Optional.ofNullable(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleTransaction)) return false;
        SimpleTransaction that = (SimpleTransaction) o;
        return getParty().equals(that.getParty()) && getAccount().equals(that.getAccount()) && getAmount().equals(that.getAmount()) && currency.equals(that.currency) && getEndToEndId().equals(that.getEndToEndId()) && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getParty(), getAccount(), getAmount(), currency, getEndToEndId(), getId());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SimpleTransaction.class.getSimpleName() + "[", "]")
                .add("party=" + party)
                .add("account=" + account)
                .add("amount=" + amount)
                .add("currency=" + currency)
                .add("endToEndId='" + endToEndId + "'")
                .add("id='" + id + "'")
                .toString();
    }
}
