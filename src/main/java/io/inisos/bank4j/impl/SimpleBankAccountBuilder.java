package io.inisos.bank4j.impl;

import io.inisos.bank4j.BankAccountBuilder;

public class SimpleBankAccountBuilder implements BankAccountBuilder {

    private String iban;
    private String bic;
    private String name;
    private String otherId;

    @Override
    public SimpleBankAccountBuilder iban(String iban) {
        this.iban = iban;
        return this;
    }

    @Override
    public SimpleBankAccountBuilder bic(String bic) {
        this.bic = bic;
        return this;
    }

    @Override
    public SimpleBankAccountBuilder name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public SimpleBankAccountBuilder otherId(String otherId) {
        this.otherId = otherId;
        return this;
    }

    @Override
    public SimpleBankAccount build() {
        return new SimpleBankAccount(iban, bic, name, otherId);
    }
}
