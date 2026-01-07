package io.inisos.bank4j.impl;

import io.inisos.bank4j.*;
import iso._20022.pain_001_003_03.*;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.iban4j.BicUtil;
import org.iban4j.IbanUtil;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import java.io.Writer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * A JAXB ISO 20022 SEPA Credit Transfer with PAIN.001.003.03
 *
 * @author Valentin Hutter
 * Based on original file JAXBCreditTransferV03 by Patrice Blanchardie
 */
public class JAXBSepaCreditTransfer003V03 implements CreditTransferOperation {

    private static final DateTimeFormatter FORMAT_AS_ID = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");
    private static final String DEFAULT_SERVICE_LEVEL = "SEPA";
    private static final String CURRENCY_EUR = "EUR";

    private final Priority instructionPriority;
    private final String serviceLevelCode;
    private final Party debtor;
    private final BankAccount debtorAccount;
    private final Collection<Transaction> transactions;
    private final String id;
    private final LocalDateTime creationDateTime;
    private final LocalDate requestedExecutionDate;
    private final ChargeBearer chargeBearer;
    private final boolean batchBooking;

    private final DatatypeFactory datatypeFactory;

    private final CustomerCreditTransferInitiationV03 customerCreditTransferInitiation;

    /**
     * Constructor
     *
     * @param instructionPriority    optional priority
     * @param serviceLevelCode       optional e.g. "SEPA"
     * @param debtor                 optional debtor
     * @param debtorAccount          debtor account
     * @param transactions           transactions (cannot contain duplicates)
     * @param id                     optional identifier, defaults to execution date and time
     * @param creationDateTime       optional message creation date and time, defaults to now
     * @param requestedExecutionDate optional requested execution date, defaults to tomorrow
     * @param chargeBearer           optional charge bearer code defines who is bearing the charges of the transfer, by default it is set to 'SLEV' (Service Level)
     */
    public JAXBSepaCreditTransfer003V03(Priority instructionPriority, String serviceLevelCode, Party debtor, BankAccount debtorAccount, Collection<Transaction> transactions, String id, LocalDateTime creationDateTime, LocalDate requestedExecutionDate, ChargeBearer chargeBearer, Boolean batchBooking) {
        this.instructionPriority = instructionPriority;
        this.serviceLevelCode = Optional.ofNullable(serviceLevelCode).orElse(DEFAULT_SERVICE_LEVEL);
        this.debtor = debtor;
        this.debtorAccount = Objects.requireNonNull(debtorAccount, "Debtor account cannot be null");
        this.transactions = requireTransaction(Objects.requireNonNull(transactions));
        this.creationDateTime = Optional.ofNullable(creationDateTime).orElse(LocalDateTime.now());
        this.requestedExecutionDate = Optional.ofNullable(requestedExecutionDate).orElse(LocalDate.now().plusDays(1));
        this.id = Optional.ofNullable(id).orElseGet(() -> FORMAT_AS_ID.format(this.creationDateTime));
        this.chargeBearer = Optional.ofNullable(chargeBearer).orElse(ChargeBearer.SLEV);
        if (this.chargeBearer != ChargeBearer.SLEV) {
            throw new IllegalArgumentException("Only SLEV charge bearer is supported for pain.001.003.03");
        }
        this.batchBooking = Optional.ofNullable(batchBooking).orElse(false);

        try {
            this.datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new XmlException(e);
        }
        this.customerCreditTransferInitiation = build();
    }

    @Override
    public void marshal(Writer writer, boolean formatted) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Document.class);

            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, formatted);
            jaxbMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
                    "urn:iso:std:iso:20022:tech:xsd:pain.001.003.03.xsd");

            jaxbMarshaller.marshal(createDocument(), writer);

        } catch (JAXBException e) {
            throw new XmlException(e);
        }
    }

    /**
     * Create the JAXB Document
     *
     * @return Document containing the credit transfer
     */
    public JAXBElement<Document> createDocument() {

        Document document = new ObjectFactory().createDocument();
        document.setCstmrCdtTrfInitn(this.customerCreditTransferInitiation);

        return new JAXBElement<>(
                new QName("urn:iso:std:iso:20022:tech:xsd:pain.001.003.03", "Document"),
                Document.class,
                document);
    }

    private CustomerCreditTransferInitiationV03 build() {

        CustomerCreditTransferInitiationV03 cti = new CustomerCreditTransferInitiationV03();

        cti.setGrpHdr(header());

        PaymentInstructionInformationSCT paymentInstructionInformation = new PaymentInstructionInformationSCT();
        paymentInstructionInformation.setPmtInfId(this.id);
        paymentInstructionInformation.setPmtMtd(PaymentMethodSCTCode.TRF);
        paymentInstructionInformation.setBtchBookg(this.batchBooking);
        paymentInstructionInformation.setNbOfTxs(String.valueOf(this.transactions.size()));
        paymentInstructionInformation.setCtrlSum(this.getTotalAmount());
        paymentInstructionInformation.setDbtr(partyIdentification(this.debtor));
        paymentInstructionInformation.setDbtrAcct(debtorCashAccount(this.debtorAccount));
        paymentInstructionInformation.setDbtrAgt(mandatoryBranchAndFinancialInstitutionIdentification(this.debtorAccount));

        PaymentTypeInformationSCT1 paymentTypeInformation = new PaymentTypeInformationSCT1();
        if (this.instructionPriority != null) {
            paymentTypeInformation.setInstrPrty(Priority2Code.fromValue(this.instructionPriority.name()));
        }
        ServiceLevelSEPA serviceLevel = new ServiceLevelSEPA();
        serviceLevel.setCd(this.serviceLevelCode);
        paymentTypeInformation.setSvcLvl(serviceLevel);
        paymentInstructionInformation.setPmtTpInf(paymentTypeInformation);

        paymentInstructionInformation.setReqdExctnDt(this.datatypeFactory.newXMLGregorianCalendar(DateTimeFormatter.ISO_LOCAL_DATE.format(requestedExecutionDate)));
        paymentInstructionInformation.setChrgBr(ChargeBearerTypeSEPACode.SLEV);

        for (Transaction transaction : this.transactions) {
            paymentInstructionInformation.getCdtTrfTxInf().add(transaction(transaction));
        }

        cti.getPmtInf().add(paymentInstructionInformation);

        return cti;
    }

    private GroupHeaderSCT header() {
        GroupHeaderSCT head = new GroupHeaderSCT();
        head.setMsgId(id);
        head.setCreDtTm(this.datatypeFactory.newXMLGregorianCalendar(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(creationDateTime)));
        head.setNbOfTxs(String.valueOf(this.transactions.size()));
        head.setCtrlSum(this.getTotalAmount());
        head.setInitgPty(partyIdentificationHeader(this.debtor));
        return head;
    }

    private CreditTransferTransactionInformationSCT transaction(Transaction transaction) {
        requireEurCurrency(transaction);

        // payment identification
        PaymentIdentificationSEPA paymentIdentificationSEPA = new PaymentIdentificationSEPA();
        paymentIdentificationSEPA.setEndToEndId(transaction.getEndToEndId());
        transaction.getId().ifPresent(paymentIdentificationSEPA::setInstrId);

        // amount
        ActiveOrHistoricCurrencyAndAmountSEPA activeOrHistoricCurrencyAndAmount = new ActiveOrHistoricCurrencyAndAmountSEPA();
        activeOrHistoricCurrencyAndAmount.setCcy(ActiveOrHistoricCurrencyCodeEUR.EUR);
        activeOrHistoricCurrencyAndAmount.setValue(transaction.getAmount());
        AmountTypeSEPA amountType = new AmountTypeSEPA();
        amountType.setInstdAmt(activeOrHistoricCurrencyAndAmount);

        // transaction
        CreditTransferTransactionInformationSCT creditTransferTransactionInformation = new CreditTransferTransactionInformationSCT();
        creditTransferTransactionInformation.setPmtId(paymentIdentificationSEPA);
        creditTransferTransactionInformation.setAmt(amountType);
        creditTransferTransactionInformation.setCdtr(partyIdentification(transaction.getParty().orElse(null)));
        creditTransferTransactionInformation.setCdtrAcct(creditorCashAccount(transaction.getAccount()));

        // remittance information
        if (!transaction.getRemittanceInformationUnstructured().isEmpty()) {
            RemittanceInformationSEPA1Choice remittanceInformation = new RemittanceInformationSEPA1Choice();
            remittanceInformation.setUstrd(transaction.getRemittanceInformationUnstructured().iterator().next());
            creditTransferTransactionInformation.setRmtInf(remittanceInformation);
        }

        transaction.getChargeBearer().ifPresent(cb -> {
            if (cb != ChargeBearer.SLEV) {
                throw new IllegalArgumentException("Only SLEV charge bearer is supported for pain.001.003.03");
            }
            creditTransferTransactionInformation.setChrgBr(ChargeBearerTypeSEPACode.SLEV);
        });

        optionalBranchAndFinancialInstitutionIdentificationOpt(transaction.getAccount()).ifPresent(creditTransferTransactionInformation::setCdtrAgt);

        return creditTransferTransactionInformation;
    }

    private Optional<BranchAndFinancialInstitutionIdentificationSEPA1> optionalBranchAndFinancialInstitutionIdentificationOpt(BankAccount bankAccount) {
        return bankAccount.getBic().map(bic -> {
            BicUtil.validate(bic);
            FinancialInstitutionIdentificationSEPA1 financialInstitutionIdentification = new FinancialInstitutionIdentificationSEPA1();
            financialInstitutionIdentification.setBIC(bic);
            BranchAndFinancialInstitutionIdentificationSEPA1 branchAndFinancialInstitutionIdentification = new BranchAndFinancialInstitutionIdentificationSEPA1();
            branchAndFinancialInstitutionIdentification.setFinInstnId(financialInstitutionIdentification);
            return branchAndFinancialInstitutionIdentification;
        });
    }

    private BranchAndFinancialInstitutionIdentificationSEPA3 mandatoryBranchAndFinancialInstitutionIdentification(BankAccount bankAccount) {
        BranchAndFinancialInstitutionIdentificationSEPA3 branchAndFinancialInstitutionIdentification = new BranchAndFinancialInstitutionIdentificationSEPA3();
        FinancialInstitutionIdentificationSEPA3 financialInstitutionIdentification = new FinancialInstitutionIdentificationSEPA3();
        Optional<String> bic = bankAccount.getBic();
        if (bic.isPresent()) {
            BicUtil.validate(bic.get());
            financialInstitutionIdentification.setBIC(bic.get());
        } else {
            OthrIdentification othrIdentification = new OthrIdentification();
            othrIdentification.setId(OthrIdentificationCode.NOTPROVIDED);
            financialInstitutionIdentification.setOthr(othrIdentification);
        }
        branchAndFinancialInstitutionIdentification.setFinInstnId(financialInstitutionIdentification);
        return branchAndFinancialInstitutionIdentification;
    }

    private PartyIdentificationSEPA2 partyIdentification(Party party) {
        PartyIdentificationSEPA2 partyIdentification = new PartyIdentificationSEPA2();
        if (party != null) {
            party.getName().ifPresent(partyIdentification::setNm);
            party.getPostalAddress().map(this::postalAddress).ifPresent(partyIdentification::setPstlAdr);
        }
        return partyIdentification;
    }

    private PartyIdentificationSEPA1 partyIdentificationHeader(Party party) {
        PartyIdentificationSEPA1 partyIdentification = new PartyIdentificationSEPA1();
        if (party != null) {
            party.getName().ifPresent(partyIdentification::setNm);
        }
        return partyIdentification;
    }

    private PostalAddressSEPA postalAddress(PostalAddress postalAddress) {
        PostalAddressSEPA postalAddressSepa = new PostalAddressSEPA();
        postalAddress.getCountry().ifPresent(postalAddressSepa::setCtry);
        int lines = 0;
        for (String addressLine : postalAddress.getAddressLines()) {
            if (lines >= 2) {
                break;
            }
            postalAddressSepa.getAdrLine().add(addressLine);
            lines++;
        }
        return postalAddressSepa;
    }

    private CashAccountSEPA1 debtorCashAccount(BankAccount bankAccount) {
        CashAccountSEPA1 cashAccount = new CashAccountSEPA1();
        cashAccount.setId(accountIdentification(bankAccount));
        return cashAccount;
    }

    private CashAccountSEPA2 creditorCashAccount(BankAccount bankAccount) {
        CashAccountSEPA2 cashAccount = new CashAccountSEPA2();
        cashAccount.setId(accountIdentification(bankAccount));
        return cashAccount;
    }

    private AccountIdentificationSEPA accountIdentification(BankAccount bankAccount) {
        AccountIdentificationSEPA accountIdentification = new AccountIdentificationSEPA();
        Optional<String> optionalIban = bankAccount.getIban();
        if (optionalIban.isPresent()) {
            String iban = optionalIban.get();
            IbanUtil.validate(iban);
            accountIdentification.setIBAN(iban);
        } else {
            throw new IllegalArgumentException("IBAN must be provided for pain.001.003.03");
        }
        return accountIdentification;
    }

    @Override
    public Priority getInstructionPriority() {
        return instructionPriority;
    }

    @Override
    public String getServiceLevelCode() {
        return serviceLevelCode;
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
    public ZonedDateTime getRequestedExecutionDateTime() {
        return null;
    }

    @Override
    public Collection<Transaction> getTransactions() {
        return transactions;
    }

    @Override
    public ChargeBearer getChargeBearer() {
        return chargeBearer;
    }

    @Override
    public boolean isBatchBooking() {
        return batchBooking;
    }

    private <T> Collection<T> requireTransaction(Collection<T> collection) {
        if (collection.isEmpty()) {
            throw new IllegalArgumentException("At least 1 transaction is required");
        }
        return collection;
    }

    private void requireEurCurrency(Transaction transaction) {
        if (!CURRENCY_EUR.equals(transaction.getCurrencyCode())) {
            throw new IllegalArgumentException("Only EUR currency is supported for pain.001.003.03");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof JAXBSepaCreditTransfer003V03)) return false;
        JAXBSepaCreditTransfer003V03 that = (JAXBSepaCreditTransfer003V03) o;
        return batchBooking == that.batchBooking && instructionPriority == that.instructionPriority && Objects.equals(serviceLevelCode, that.serviceLevelCode) && Objects.equals(debtor, that.debtor) && Objects.equals(debtorAccount, that.debtorAccount) && Objects.equals(transactions, that.transactions) && Objects.equals(id, that.id) && Objects.equals(creationDateTime, that.creationDateTime) && Objects.equals(requestedExecutionDate, that.requestedExecutionDate) && chargeBearer == that.chargeBearer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(instructionPriority, serviceLevelCode, debtor, debtorAccount, transactions, id, creationDateTime, requestedExecutionDate, chargeBearer, batchBooking);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", JAXBSepaCreditTransfer003V03.class.getSimpleName() + "[", "]")
                .add("instructionPriority=" + instructionPriority)
                .add("serviceLevelCode='" + serviceLevelCode + "'")
                .add("debtor=" + debtor)
                .add("debtorAccount=" + debtorAccount)
                .add("id='" + id + "'")
                .add("creationDateTime=" + creationDateTime)
                .add("requestedExecutionDate=" + requestedExecutionDate)
                .add("chargeBearer=" + chargeBearer)
                .add("batchBooking=" + batchBooking)
                .toString();
    }
}
