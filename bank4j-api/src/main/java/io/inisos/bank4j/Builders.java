package io.inisos.bank4j;

/**
 * @author Patrice Blanchardie
 */
public interface Builders {
    PartyBuilder party();

    PostalAddressBuilder postalAddress();

    BankAccountBuilder bankAccount();

    TransactionBuilder transaction();

    CreditTransferBuilder creditTransferSepa();

    CreditTransferBuilder creditTransfer();
}
