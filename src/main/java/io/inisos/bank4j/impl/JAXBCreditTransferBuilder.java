package io.inisos.bank4j.impl;

import io.inisos.bank4j.*;
import iso.std.iso._20022.tech.xsd.pain_001_001.ChargeBearerType1Code;

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
    private ChargeBearerType1Code chargeBearerCode;
    private Boolean batchBooking;

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
    public CreditTransferOperationBuilder chargeBearerCode(ChargeBearerType1Code chargeBearerCode) {
        this.chargeBearerCode = chargeBearerCode;
        return this;
    }

    @Override
    public CreditTransferOperationBuilder batchBooking(Boolean batchBooking) {
        this.batchBooking = batchBooking;
        return this;
    }

    @Override
    public CreditTransferOperation build() {
        return new JAXBCreditTransfer(serviceLevelCode, debtor, debtorAccount, transactions, id, creationDateTime, requestedExecutionDate, chargeBearerCode, batchBooking);
    }
}
