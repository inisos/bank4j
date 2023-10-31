package io.inisos.bank4j.simple;

import io.inisos.bank4j.BankAccount;
import io.inisos.bank4j.Party;
import io.inisos.bank4j.TransactionBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

/**
 * @author Patrice Blanchardie
 */
public class SimpleTransactionBuilder implements TransactionBuilder {
    private Party party;
    private BankAccount account;
    private BigDecimal amount;
    private Currency currency;
    private String endToEndId;
    private String id;
    private final List<BankAccount> intermediaryAgents = new ArrayList<>();

    @Override
    public SimpleTransactionBuilder party(Party party) {
        this.party = party;
        return this;
    }

    @Override
    public SimpleTransactionBuilder account(BankAccount account) {
        this.account = account;
        return this;
    }

    @Override
    public SimpleTransactionBuilder amount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public SimpleTransactionBuilder currency(Currency currency) {
        this.currency = currency;
        return this;
    }

    @Override
    public SimpleTransactionBuilder endToEndId(String endToEndId) {
        this.endToEndId = endToEndId;
        return this;
    }

    @Override
    public SimpleTransactionBuilder id(String id) {
        this.id = id;
        return this;
    }

    @Override
    public TransactionBuilder intermediaryAgents(List<BankAccount> intermediaryAgents) {
        this.intermediaryAgents.addAll(intermediaryAgents);
        return this;
    }

    @Override
    public TransactionBuilder intermediaryAgent(BankAccount intermediaryAgent) {
        this.intermediaryAgents.add(intermediaryAgent);
        return this;
    }

    @Override
    public SimpleTransaction build() {
        return new SimpleTransaction(party, account, amount, currency, endToEndId, id, intermediaryAgents);
    }
}
