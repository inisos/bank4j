package io.inisos.bank4j.impl;

import io.inisos.bank4j.BankAccount;
import io.inisos.bank4j.validator.constraints.BIC;
import io.inisos.bank4j.validator.constraints.IBAN;

import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * Simple Bank Account with IBAN and BIC validation
 *
 * @author Patrice Blanchardie
 */
public class SimpleBankAccount implements BankAccount {

    @IBAN
    private final String iban;

    @BIC
    private final String bic;

    private final String name;

    private final String otherId;

    public SimpleBankAccount(String iban, String bic, String name, String otherId) {
        this.iban = iban;
        this.bic = bic;
        this.name = name;
        this.otherId = otherId;
        requireIdentification();
    }

    @Override
    public Optional<String> getIban() {
        return Optional.ofNullable(iban);
    }

    @Override
    public Optional<String> getBic() {
        return Optional.ofNullable(bic);
    }

    @Override
    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    @Override
    public Optional<String> getOtherId() {
        return Optional.ofNullable(otherId);
    }

    private void requireIdentification() {
        if ((iban == null || iban.isEmpty()) && (otherId == null || otherId.isEmpty())) {
            throw new IllegalArgumentException("IBAN or otherId must be provided");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleBankAccount)) return false;
        SimpleBankAccount that = (SimpleBankAccount) o;
        return Objects.equals(iban, that.iban) && Objects.equals(otherId, that.otherId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(iban, otherId);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SimpleBankAccount.class.getSimpleName() + "[", "]")
                .add("iban='" + iban + "'")
                .add("bic='" + bic + "'")
                .add("name='" + name + "'")
                .add("otherId='" + otherId + "'")
                .toString();
    }
}
