package io.inisos.bank4j.simple;

import io.inisos.bank4j.BankAccount;
import io.inisos.bank4j.CreditTransfer;
import io.inisos.bank4j.Party;
import io.inisos.bank4j.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * A JAXB ISO 20022 Credit Transfer with PAIN.001.001.03
 *
 * @author Patrice Blanchardie
 */
public class SimpleCreditTransfer implements CreditTransfer {

    private static final DateTimeFormatter FORMAT_AS_ID = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");

    private final String serviceLevelCode;
    private final Party debtor;
    private final BankAccount debtorAccount;
    private final Collection<Transaction> transactions;
    private final String id;
    private final LocalDateTime creationDateTime;
    private final LocalDate requestedExecutionDate;

    /**
     * Constructor
     *
     * @param serviceLevelCode       optional e.g. "SEPA"
     * @param debtor                 optional debtor
     * @param debtorAccount          debtor account
     * @param transactions           transactions (cannot contain duplicates)
     * @param id                     optional identifier, defaults to execution date and time
     * @param creationDateTime       optional message creation date and time, defaults to now
     * @param requestedExecutionDate optional requested execution date and time, defaults to tomorrow
     */
    public SimpleCreditTransfer(String serviceLevelCode, Party debtor, BankAccount debtorAccount, Collection<Transaction> transactions, String id, LocalDateTime creationDateTime, LocalDate requestedExecutionDate) {
        this.serviceLevelCode = serviceLevelCode;
        this.debtor = debtor;
        this.debtorAccount = Objects.requireNonNull(debtorAccount, "Debtor account cannot be null");
        this.transactions = requireTransaction(Objects.requireNonNull(transactions));
        this.creationDateTime = Optional.ofNullable(creationDateTime).orElse(LocalDateTime.now());
        this.requestedExecutionDate = Optional.ofNullable(requestedExecutionDate).orElse(LocalDate.now().plusDays(1));
        this.id = Optional.ofNullable(id).orElseGet(() -> FORMAT_AS_ID.format(this.creationDateTime));
    }

    @Override
    public Optional<String> getServiceLevelCode() {
        return Optional.ofNullable(serviceLevelCode);
    }

    @Override
    public Optional<Party> getDebtor() {
        return Optional.ofNullable(debtor);
    }

    @Override
    public BankAccount getDebtorAccount() {
        return debtorAccount;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    @Override
    public LocalDate getRequestedExecutionDate() {
        return requestedExecutionDate;
    }

    @Override
    public Collection<Transaction> getTransactions() {
        return transactions;
    }

    private <T> Collection<T> requireTransaction(Collection<T> collection) {
        if (collection.isEmpty()) {
            throw new IllegalArgumentException("At least 1 transaction is required");
        }
        return collection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleCreditTransfer)) return false;
        SimpleCreditTransfer that = (SimpleCreditTransfer) o;
        return serviceLevelCode.equals(that.serviceLevelCode) && debtor.equals(that.debtor) && getTransactions().equals(that.getTransactions()) && id.equals(that.id) && creationDateTime.equals(that.creationDateTime) && requestedExecutionDate.equals(that.requestedExecutionDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceLevelCode, debtor, getTransactions(), id, creationDateTime, requestedExecutionDate);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SimpleCreditTransfer.class.getSimpleName() + "[", "]")
                .add("serviceLevelCode='" + serviceLevelCode + "'")
                .add("debtor=" + debtor)
                .add("debtorAccount=" + debtorAccount)
                .add("id='" + id + "'")
                .add("creationDateTime=" + creationDateTime)
                .add("requestedExecutionDate=" + requestedExecutionDate)
                .toString();
    }
}
