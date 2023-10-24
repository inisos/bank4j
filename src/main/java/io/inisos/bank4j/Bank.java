package io.inisos.bank4j;

import io.inisos.bank4j.impl.JAXBCreditTransferBuilder;
import io.inisos.bank4j.impl.SimpleTransactionBuilder;

/**
 * Builder Factory
 */
public class Bank {

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
