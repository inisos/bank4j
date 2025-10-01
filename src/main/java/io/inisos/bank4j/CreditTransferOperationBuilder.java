package io.inisos.bank4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

import iso.std.iso._20022.tech.xsd.pain_001_001.ChargeBearerType1Code;

public interface CreditTransferOperationBuilder {
    /**
     * SEPA builder
     * Shortcut for serviceLevelCode("SEPA")
     *
     * @return a builder with the service level code "SEPA"
     */
    default CreditTransferOperationBuilder sepa() {
        return serviceLevelCode("SEPA");
    }

    CreditTransferOperationBuilder serviceLevelCode(String serviceLevelCode);

    CreditTransferOperationBuilder debtor(Party debtor);

    CreditTransferOperationBuilder debtorAccount(BankAccount debtorAccount);

    CreditTransferOperationBuilder transactions(Collection<Transaction> transactions);

    CreditTransferOperationBuilder transaction(Transaction transaction);

    CreditTransferOperationBuilder id(String id);

    CreditTransferOperationBuilder creationDateTime(LocalDateTime creationDateTime);

    CreditTransferOperationBuilder requestedExecutionDate(LocalDate requestedExecutionDate);

    CreditTransferOperationBuilder chargeBearerCode(ChargeBearerType1Code chargeBearerCode);

    CreditTransferOperationBuilder batchBooking(Boolean batchBooking);

    CreditTransferOperation build();
}
