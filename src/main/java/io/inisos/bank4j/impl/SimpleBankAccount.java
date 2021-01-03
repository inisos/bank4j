package io.inisos.bank4j.impl;

import io.inisos.bank4j.BankAccount;
import io.inisos.bank4j.validator.constraints.BIC;
import io.inisos.bank4j.validator.constraints.IBAN;

import javax.validation.constraints.NotBlank;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Simple Bank Account with IBAN and BIC validation
 *
 * @author Patrice Blanchardie
 */
public class SimpleBankAccount implements BankAccount {

    @NotBlank
    private String name;

    @IBAN
    @NotBlank
    private String iban;

    @BIC
    private String bic;

    public SimpleBankAccount(String name, String iban) {
        this(name, iban, null);
    }

    public SimpleBankAccount(String name, String iban, String bic) {
        this.name = name;
        this.iban = iban;
        this.bic = bic;
    }

    @Override
    public String getName() {
        return name;
    }

    public SimpleBankAccount setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getIban() {
        return iban;
    }

    public SimpleBankAccount setIban(String iban) {
        this.iban = iban;
        return this;
    }

    @Override
    public String getBic() {
        return bic;
    }

    public SimpleBankAccount setBic(String bic) {
        this.bic = bic;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleBankAccount)) return false;
        SimpleBankAccount that = (SimpleBankAccount) o;
        return getName().equals(that.getName()) && getIban().equals(that.getIban()) && Objects.equals(getBic(), that.getBic());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getIban(), getBic());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SimpleBankAccount.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .toString();
    }
}
