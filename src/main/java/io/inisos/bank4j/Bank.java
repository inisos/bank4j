package io.inisos.bank4j;

import io.inisos.bank4j.impl.JAXBCreditTransfer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Operation Factory
 */
public class Bank {

    public static CreditTransfer creditTransferSepa(BankAccount debtor, Collection<Transaction> transactions) {
        return new JAXBCreditTransfer("SEPA", debtor, transactions, null, null, null);
    }

    public static CreditTransfer creditTransferSepa(BankAccount debtor, Collection<Transaction> transactions, LocalDateTime creationDateTime) {
        return new JAXBCreditTransfer("SEPA", debtor, transactions, null, creationDateTime, null);
    }

    public static CreditTransfer creditTransferSepa(BankAccount debtor, Collection<Transaction> transactions, LocalDate requestedExecutionDate) {
        return new JAXBCreditTransfer("SEPA", debtor, transactions, null, null, requestedExecutionDate);
    }

    public static CreditTransfer creditTransferSepa(BankAccount debtor, Collection<Transaction> transactions, LocalDateTime creationDateTime, LocalDate requestedExecutionDate) {
        return new JAXBCreditTransfer("SEPA", debtor, transactions, null, creationDateTime, requestedExecutionDate);
    }

    public static CreditTransfer creditTransferSepa(BankAccount debtor, Collection<Transaction> transactions, String id, LocalDateTime creationDateTime) {
        return new JAXBCreditTransfer("SEPA", debtor, transactions, id, creationDateTime, null);
    }

    public static CreditTransfer creditTransferSepa(BankAccount debtor, Collection<Transaction> transactions, String id, LocalDateTime creationDateTime, LocalDate requestedExecutionDate) {
        return new JAXBCreditTransfer("SEPA", debtor, transactions, id, creationDateTime, requestedExecutionDate);
    }

    public static CreditTransfer creditTransfer(String serviceLevelCode, BankAccount debtor, Collection<Transaction> transactions, String id, LocalDateTime creationDateTime) {
        return creditTransfer(serviceLevelCode, debtor, transactions, id, creationDateTime, null);
    }

    public static CreditTransfer creditTransfer(String serviceLevelCode, BankAccount debtor, Collection<Transaction> transactions, String id, LocalDateTime creationDateTime, LocalDate requestedExecutionDate) {
        return new JAXBCreditTransfer(serviceLevelCode, debtor, transactions, id, creationDateTime, requestedExecutionDate);
    }

    private Bank() {
    }

}
