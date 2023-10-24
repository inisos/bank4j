package io.inisos.bank4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

/**
 * A Credit Transfer
 */
public interface CreditTransfer {

    BankAccount getDebtor();

    String getId();

    LocalDateTime getCreationDateTime();

    LocalDate getRequestedExecutionDate();

    Collection<Transaction> getTransactions();

    default BigDecimal getTotalAmount() {
        return getTransactions()
                .stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
