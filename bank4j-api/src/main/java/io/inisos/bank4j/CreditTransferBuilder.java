package io.inisos.bank4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

/**
 * @author Patrice Blanchardie
 */
public interface CreditTransferBuilder {
    /**
     * SEPA builder
     * Shortcut for serviceLevelCode("SEPA")
     *
     * @return a builder with the service level code "SEPA"
     */
    default CreditTransferBuilder sepa() {
        return serviceLevelCode("SEPA");
    }

    CreditTransferBuilder serviceLevelCode(String serviceLevelCode);

    CreditTransferBuilder debtor(Party debtor);

    CreditTransferBuilder debtorAccount(BankAccount debtorAccount);

    CreditTransferBuilder transactions(Collection<Transaction> transactions);

    CreditTransferBuilder transaction(Transaction transaction);

    CreditTransferBuilder id(String id);

    CreditTransferBuilder creationDateTime(LocalDateTime creationDateTime);

    CreditTransferBuilder requestedExecutionDate(LocalDate requestedExecutionDate);

    CreditTransfer build();
}
