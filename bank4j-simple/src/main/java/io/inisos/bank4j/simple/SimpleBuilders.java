package io.inisos.bank4j.simple;

import io.inisos.bank4j.Builders;

/**
 * Simple Builder Factory
 *
 * @author Patrice Blanchardie
 */
public class SimpleBuilders implements Builders {

    @Override
    public SimplePartyBuilder party() {
        return new SimplePartyBuilder();
    }

    @Override
    public SimplePostalAddressBuilder postalAddress() {
        return new SimplePostalAddressBuilder();
    }

    @Override
    public SimpleBankAccountBuilder bankAccount() {
        return new SimpleBankAccountBuilder();
    }

    @Override
    public SimpleTransactionBuilder transaction() {
        return new SimpleTransactionBuilder();
    }

    @Override
    public SimpleCreditTransferBuilder creditTransferSepa() {
        return (SimpleCreditTransferBuilder) new SimpleCreditTransferBuilder().sepa();
    }

    @Override
    public SimpleCreditTransferBuilder creditTransfer() {
        return new SimpleCreditTransferBuilder();
    }

    public static Builders get() {
        return new SimpleBuilders();
    }

}
