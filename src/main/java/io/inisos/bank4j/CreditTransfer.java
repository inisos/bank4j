package io.inisos.bank4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Optional;

/**
 * A Credit Transfer
 */
public interface CreditTransfer {

    Priority getInstructionPriority();

    Optional<Party> getDebtor();

    BankAccount getDebtorAccount();

    String getId();

    LocalDateTime getCreationDateTime();

    LocalDate getRequestedExecutionDate();

    ZonedDateTime getRequestedExecutionDateTime();

    Collection<Transaction> getTransactions();

    default BigDecimal getTotalAmount() {
        return getTransactions()
                .stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
