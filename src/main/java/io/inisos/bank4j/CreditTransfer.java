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

    String getServiceLevelCode();

    Optional<Party> getDebtor();

    BankAccount getDebtorAccount();

    String getId();

    LocalDateTime getCreationDateTime();

    LocalDate getRequestedExecutionDate();

    ZonedDateTime getRequestedExecutionDateTime();

    Collection<Transaction> getTransactions();

    ChargeBearer getChargeBearer();

    boolean isBatchBooking();

    /**
     * @return the sum of all transaction amounts, regardless of currencies
     * @deprecated use {@link #getControlSum()} instead
     */
    @Deprecated
    default BigDecimal getTotalAmount() {
        return getControlSum();
    }

    /**
     * @return the sum of all transaction amounts, regardless of currencies
     */
    default BigDecimal getControlSum() {
        return getTransactions()
                .stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
