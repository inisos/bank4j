package io.inisos.bank4j;

import io.inisos.bank4j.impl.*;

/**
 * Builder Factory
 */
public class Bank {

    public static PartyBuilder simpleParty() {
        return new SimplePartyBuilder();
    }

    public static PostalAddressBuilder simplePostalAddress() {
        return new SimplePostalAddressBuilder();
    }

    public static BankAccountBuilder simpleBankAccount() {
        return new SimpleBankAccountBuilder();
    }

    public static TransactionBuilder simpleTransaction() {
        return new SimpleTransactionBuilder();
    }

    public static CreditTransferOperationBuilder jaxbCreditTransferSepa() {
        return new JAXBCreditTransferBuilder().sepa();
    }

    public static CreditTransferOperationBuilder jaxbCreditTransfer() {
        return new JAXBCreditTransferBuilder();
    }

    private Bank() {
    }

}
