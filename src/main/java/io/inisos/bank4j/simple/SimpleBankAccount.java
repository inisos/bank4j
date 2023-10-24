package io.inisos.bank4j.simple;

import io.inisos.bank4j.BankAccount;
import io.inisos.bank4j.validator.constraints.BIC;
import io.inisos.bank4j.validator.constraints.IBAN;

import javax.validation.constraints.NotBlank;
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
    @NotBlank
    private final String iban;

    @BIC
    private final String bic;

    private final String name;

    public SimpleBankAccount(String iban, String bic, String name) {
        this.iban = Objects.requireNonNull(iban, "IBAN is required");
        this.bic = bic;
        this.name = name;
    }

    @Override
    public String getIban() {
        return iban;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleBankAccount)) return false;
        SimpleBankAccount that = (SimpleBankAccount) o;
        return Objects.equals(iban, that.iban);
    }

    @Override
    public int hashCode() {
        return Objects.hash(iban);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SimpleBankAccount.class.getSimpleName() + "[", "]")
                .add("iban='" + iban + "'")
                .add("bic='" + bic + "'")
                .add("name='" + name + "'")
                .toString();
    }
}
