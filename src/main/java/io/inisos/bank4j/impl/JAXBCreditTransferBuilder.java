package io.inisos.bank4j.impl;

import io.inisos.bank4j.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;

public class JAXBCreditTransferBuilder implements CreditTransferOperationBuilder {
    private CustomerCreditTransferInitiationVersion version;
    private Priority instructionPriority;
    private String serviceLevelCode;
    private Party debtor;
    private BankAccount debtorAccount;
    private final Collection<Transaction> transactions = new ArrayList<>();
    private String id;
    private LocalDateTime creationDateTime;
    private LocalDate requestedExecutionDate;
    private ZonedDateTime requestedExecutionDateTime;
    private ChargeBearer chargeBearer;
    private Boolean batchBooking;

    public JAXBCreditTransferBuilder version(CustomerCreditTransferInitiationVersion version) {
        this.version = version;
        return this;
    }

    @Override
    public JAXBCreditTransferBuilder instructionPriority(Priority instructionPriority) {
        this.instructionPriority = instructionPriority;
        return this;
    }

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
        if (requestedExecutionDateTime != null) {
            throw new IllegalStateException("Only one of requestedExecutionDate and requestedExecutionDateTime can be set, not both");
        }
        this.requestedExecutionDate = requestedExecutionDate;
        return this;
    }

    @Override
    public JAXBCreditTransferBuilder requestedExecutionDateTime(ZonedDateTime requestedExecutionDateTime) {
        if (requestedExecutionDate != null) {
            throw new IllegalStateException("Only one of requestedExecutionDate and requestedExecutionDateTime can be set, not both");
        }
        if (version != CustomerCreditTransferInitiationVersion.V09) {
            throw new IllegalStateException("Only version 09 supports requestedExecutionDateTime");
        }
        this.requestedExecutionDateTime = requestedExecutionDateTime;
        return this;
    }

    @Override
    public CreditTransferOperationBuilder chargeBearer(ChargeBearer chargeBearer) {
        this.chargeBearer = chargeBearer;
        return this;
    }

    @Override
    public CreditTransferOperationBuilder batchBooking(Boolean batchBooking) {
        this.batchBooking = batchBooking;
        return this;
    }

    @Override
    public CreditTransferOperation build() {
        if (version == null) {
            throw new IllegalStateException("Version must be set");
        }
        switch (version) {
            case V03:
                return new JAXBCreditTransferV03(instructionPriority, serviceLevelCode, debtor, debtorAccount, transactions, id, creationDateTime, requestedExecutionDate, chargeBearer, batchBooking);
            case V09:
                return new JAXBCreditTransferV09(instructionPriority, serviceLevelCode, debtor, debtorAccount, transactions, id, creationDateTime, requestedExecutionDate, requestedExecutionDateTime, chargeBearer, batchBooking);
            case V03_CH_02:
                return new JAXBCreditTransferV03Ch02(instructionPriority, serviceLevelCode, debtor, debtorAccount, transactions, id, creationDateTime, requestedExecutionDate, chargeBearer, batchBooking);
            default:
                throw new IllegalArgumentException("Unsupported version: " + version);
        }
    }
}
