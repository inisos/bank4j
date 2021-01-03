package io.inisos.bank4j;

import io.inisos.bank4j.impl.JAXBCreditTransfer;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Operation Factory
 */
public class Bank {

    public static CreditTransfer creditTransferSepa(BankAccount debtor, Collection<Transaction> transactions) {
        return new JAXBCreditTransfer("SEPA", debtor, transactions, null, null);
    }

    public static CreditTransfer creditTransferSepa(BankAccount debtor, Collection<Transaction> transactions, LocalDateTime dateTime) {
        return new JAXBCreditTransfer("SEPA", debtor, transactions, null, dateTime);
    }

    public static CreditTransfer creditTransferSepa(BankAccount debtor, Collection<Transaction> transactions, String id, LocalDateTime dateTime) {
        return new JAXBCreditTransfer("SEPA", debtor, transactions, id, dateTime);
    }

    public static CreditTransfer creditTransfer(String serviceLevelCode, BankAccount debtor, Collection<Transaction> transactions, String id, LocalDateTime dateTime) {
        return new JAXBCreditTransfer(serviceLevelCode, debtor, transactions, id, dateTime);
    }

    private Bank() {
    }

}
