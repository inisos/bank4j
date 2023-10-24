package io.inisos.bank4j.impl;

import io.inisos.bank4j.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

public class JAXBCreditTransferBuilder implements CreditTransferOperationBuilder {
    private String serviceLevelCode;
    private Party debtor;
    private BankAccount debtorAccount;
    private final Collection<Transaction> transactions = new ArrayList<>();
    private String id;
    private LocalDateTime creationDateTime;
    private LocalDate requestedExecutionDate;

    @Override
    public CreditTransferOperationBuilder serviceLevelCode(String serviceLevelCode) {
        this.serviceLevelCode = serviceLevelCode;
        return this;
    }

    @Override
    public CreditTransferOperationBuilder debtor(Party debtor) {
        this.debtor = debtor;
        return this;
    }

    @Override
    public CreditTransferOperationBuilder debtorAccount(BankAccount debtorAccount) {
        this.debtorAccount = debtorAccount;
        return this;
    }

    @Override
    public CreditTransferOperationBuilder transactions(Collection<Transaction> transactions) {
        this.transactions.addAll(transactions);
        return this;
    }

    @Override
    public CreditTransferOperationBuilder transaction(Transaction transaction) {
        this.transactions.add(transaction);
        return this;
    }

    @Override
    public CreditTransferOperationBuilder id(String id) {
        this.id = id;
        return this;
    }

    @Override
    public CreditTransferOperationBuilder creationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
        return this;
    }

    @Override
    public CreditTransferOperationBuilder requestedExecutionDate(LocalDate requestedExecutionDate) {
        this.requestedExecutionDate = requestedExecutionDate;
        return this;
    }

    @Override
    public CreditTransferOperation build() {
        return new JAXBCreditTransfer(serviceLevelCode, debtor, debtorAccount, transactions, id, creationDateTime, requestedExecutionDate);
    }
}
