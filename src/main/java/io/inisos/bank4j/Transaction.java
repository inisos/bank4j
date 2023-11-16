package io.inisos.bank4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import iso.std.iso._20022.tech.xsd.pain_001_001.ChargeBearerType1Code;

/**
 * Transaction
 */
public interface Transaction {

    /**
     * @return third party identification
     */
    Optional<Party> getParty();

    /**
     * @return cash account
     */
    BankAccount getAccount();

    /**
     * @return transaction amount
     */
    BigDecimal getAmount();

    /**
     * @return transaction currency
     */
    String getCurrencyCode();

    /**
     * @return end-to-end identifier
     */
    String getEndToEndId();

    /**
     * @return optional identifier
     */
    Optional<String> getId();

    /**
     * @return who bears the transaction costs
     */
    Optional<ChargeBearerType1Code> getChargeBearerCode();

    /**
     * @return intermediary agents
     */
    List<BankAccount> getIntermediaryAgents();
}
