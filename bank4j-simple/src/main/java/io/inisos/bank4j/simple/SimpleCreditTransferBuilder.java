package io.inisos.bank4j.simple;

import io.inisos.bank4j.BankAccount;
import io.inisos.bank4j.CreditTransferBuilder;
import io.inisos.bank4j.Party;
import io.inisos.bank4j.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Patrice Blanchardie
 */
public class SimpleCreditTransferBuilder implements CreditTransferBuilder {
    private String serviceLevelCode;
    private Party debtor;
    private BankAccount debtorAccount;
    private final Collection<Transaction> transactions = new ArrayList<>();
    private String id;
    private LocalDateTime creationDateTime;
    private LocalDate requestedExecutionDate;

    @Override
    public SimpleCreditTransferBuilder serviceLevelCode(String serviceLevelCode) {
        this.serviceLevelCode = serviceLevelCode;
        return this;
    }

    @Override
    public SimpleCreditTransferBuilder debtor(Party debtor) {
        this.debtor = debtor;
        return this;
    }

    @Override
    public SimpleCreditTransferBuilder debtorAccount(BankAccount debtorAccount) {
        this.debtorAccount = debtorAccount;
        return this;
    }

    @Override
    public SimpleCreditTransferBuilder transactions(Collection<Transaction> transactions) {
        this.transactions.addAll(transactions);
        return this;
    }

    @Override
    public SimpleCreditTransferBuilder transaction(Transaction transaction) {
        this.transactions.add(transaction);
        return this;
    }

    @Override
    public SimpleCreditTransferBuilder id(String id) {
        this.id = id;
        return this;
    }

    @Override
    public SimpleCreditTransferBuilder creationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
        return this;
    }

    @Override
    public SimpleCreditTransferBuilder requestedExecutionDate(LocalDate requestedExecutionDate) {
        this.requestedExecutionDate = requestedExecutionDate;
        return this;
    }

    @Override
    public SimpleCreditTransfer build() {
        return new SimpleCreditTransfer(serviceLevelCode, debtor, debtorAccount, transactions, id, creationDateTime, requestedExecutionDate);
    }
}
