package io.inisos.bank4j;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Set;

import iso.std.iso._20022.tech.xsd.pain_001_001.ChargeBearerType1Code;

public interface TransactionBuilder {

    TransactionBuilder party(Party party);

    TransactionBuilder account(BankAccount account);

    TransactionBuilder amount(BigDecimal amount);

    /**
     * Shortcut for {@link #amount(BigDecimal)}
     *
     * @param amount amount as a string
     * @return this
     */
    default TransactionBuilder amount(String amount) {
        return amount(new BigDecimal(amount));
    }

    TransactionBuilder currency(Currency currency);

    /**
     * Shortcut for {@link #currency(Currency)}
     *
     * @param currencyCode currency code
     * @return this
     */
    default TransactionBuilder currency(String currencyCode) {
        return currency(Currency.getInstance(currencyCode));
    }

    TransactionBuilder endToEndId(String endToEndId);

    TransactionBuilder id(String id);

    TransactionBuilder chargeBearerCode(ChargeBearerType1Code chargeBearerCode);

    TransactionBuilder intermediaryAgents(List<BankAccount> intermediaryAgents);

    TransactionBuilder intermediaryAgent(BankAccount intermediaryAgent);

    TransactionBuilder remittanceInformationUnstructured(Set<String> remittanceInformationUnstructured);

    Transaction build();
}
