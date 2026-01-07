package io.inisos.bank4j.impl;

import io.inisos.bank4j.*;
import iso._20022.pain_001_001_03_ch_02.*;
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
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * A JAXB ISO 20022 Credit Transfer with PAIN.001.001.03.CH.02
 *
 * @author Valentin Hutter
 * Based on original file JAXBCreditTransferV03 by Patrice Blanchardie
 */
public class JAXBCreditTransferV03Ch02 implements CreditTransferOperation {

    private static final DateTimeFormatter FORMAT_AS_ID = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");
    private static final String SCHEMA_LOCATION = "http://www.six-interbank-clearing.com/de/pain.001.001.03.ch.02.xsd pain.001.001.03.ch.02.xsd";

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

    private final CustomerCreditTransferInitiationV03CH customerCreditTransferInitiation;

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
    public JAXBCreditTransferV03Ch02(Priority instructionPriority, String serviceLevelCode, Party debtor, BankAccount debtorAccount, Collection<Transaction> transactions, String id, LocalDateTime creationDateTime, LocalDate requestedExecutionDate, ChargeBearer chargeBearer, Boolean batchBooking) {
        this.instructionPriority = instructionPriority;
        this.serviceLevelCode = serviceLevelCode;
        this.debtor = debtor;
        this.debtorAccount = Objects.requireNonNull(debtorAccount, "Debtor account cannot be null");
        this.transactions = requireTransaction(Objects.requireNonNull(transactions));
        this.creationDateTime = Optional.ofNullable(creationDateTime).orElse(LocalDateTime.now());
        this.requestedExecutionDate = Optional.ofNullable(requestedExecutionDate).orElse(LocalDate.now().plusDays(1));
        this.id = Optional.ofNullable(id).orElseGet(() -> FORMAT_AS_ID.format(this.creationDateTime));
        this.chargeBearer = Optional.ofNullable(chargeBearer).orElse(ChargeBearer.SLEV);
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
            jaxbMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, SCHEMA_LOCATION);

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
                new QName(SCHEMA_LOCATION, "Document"),
                Document.class,
                document);
    }

    private CustomerCreditTransferInitiationV03CH build() {

        CustomerCreditTransferInitiationV03CH cti = new CustomerCreditTransferInitiationV03CH();

        cti.setGrpHdr(header());

        PaymentInstructionInformation3CH paymentInstructionInformation = new PaymentInstructionInformation3CH();
        paymentInstructionInformation.setPmtInfId(this.id);
        paymentInstructionInformation.setPmtMtd(PaymentMethod3Code.TRF);
        paymentInstructionInformation.setBtchBookg(this.batchBooking);
        paymentInstructionInformation.setNbOfTxs(String.valueOf(this.transactions.size()));
        paymentInstructionInformation.setCtrlSum(this.getTotalAmount());
        paymentInstructionInformation.setDbtr(partyIdentification(this.debtor));
        paymentInstructionInformation.setDbtrAcct(debtorCashAccount(this.debtorAccount));
        paymentInstructionInformation.setDbtrAgt(mandatoryBranchAndFinancialInstitutionIdentification(this.debtorAccount));

        PaymentTypeInformation19CH paymentTypeInformation = new PaymentTypeInformation19CH();
        if (this.instructionPriority != null) {
            paymentTypeInformation.setInstrPrty(Priority2Code.fromValue(this.instructionPriority.name()));
        }
        if (this.serviceLevelCode != null) {
            ServiceLevel8Choice serviceLevel = new ServiceLevel8Choice();
            serviceLevel.setCd(this.serviceLevelCode);
            paymentTypeInformation.setSvcLvl(serviceLevel);
        }
        paymentInstructionInformation.setPmtTpInf(paymentTypeInformation);

        paymentInstructionInformation.setReqdExctnDt(this.datatypeFactory.newXMLGregorianCalendar(DateTimeFormatter.ISO_LOCAL_DATE.format(requestedExecutionDate)));

        paymentInstructionInformation.setChrgBr(ChargeBearerType1Code.fromValue(this.chargeBearer.name()));

        for (Transaction transaction : this.transactions) {
            paymentInstructionInformation.getCdtTrfTxInf().add(transaction(transaction));
        }

        cti.getPmtInf().add(paymentInstructionInformation);

        return cti;
    }

    private GroupHeader32CH header() {
        GroupHeader32CH head = new GroupHeader32CH();
        head.setMsgId(id);
        head.setCreDtTm(this.datatypeFactory.newXMLGregorianCalendar(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(creationDateTime)));
        head.setNbOfTxs(String.valueOf(this.transactions.size()));
        head.setCtrlSum(this.getTotalAmount());
        head.setInitgPty(partyIdentificationHeader(this.debtor));
        return head;
    }

    private CreditTransferTransactionInformation10CH transaction(Transaction transaction) {

        // payment identification
        PaymentIdentification1 paymentIdentification = new PaymentIdentification1();
        paymentIdentification.setEndToEndId(transaction.getEndToEndId());
        transaction.getId().ifPresent(paymentIdentification::setInstrId);

        // amount
        ActiveOrHistoricCurrencyAndAmount activeOrHistoricCurrencyAndAmount = new ActiveOrHistoricCurrencyAndAmount();
        activeOrHistoricCurrencyAndAmount.setCcy(transaction.getCurrencyCode());
        activeOrHistoricCurrencyAndAmount.setValue(transaction.getAmount());
        AmountType3Choice amountType = new AmountType3Choice();
        amountType.setInstdAmt(activeOrHistoricCurrencyAndAmount);

        // transaction
        CreditTransferTransactionInformation10CH creditTransferTransactionInformation = new CreditTransferTransactionInformation10CH();
        creditTransferTransactionInformation.setPmtId(paymentIdentification);
        creditTransferTransactionInformation.setAmt(amountType);
        PartyIdentification32CHName creditor = partyIdentificationName(transaction.getParty().orElse(null));
        if (creditor != null) {
            creditTransferTransactionInformation.setCdtr(creditor);
        }
        creditTransferTransactionInformation.setCdtrAcct(creditorCashAccount(transaction.getAccount()));

        // remittance information
        if (!transaction.getRemittanceInformationUnstructured().isEmpty()) {
            RemittanceInformation5CH remittanceInformation = new RemittanceInformation5CH();
            remittanceInformation.setUstrd(transaction.getRemittanceInformationUnstructured().iterator().next());
            creditTransferTransactionInformation.setRmtInf(remittanceInformation);
        }

        transaction.getChargeBearer()
                .map(cb -> ChargeBearerType1Code.fromValue(cb.name()))
                .ifPresent(creditTransferTransactionInformation::setChrgBr);

        optionalBranchAndFinancialInstitutionIdentificationOpt(transaction.getAccount()).ifPresent(creditTransferTransactionInformation::setCdtrAgt);
        Iterator<BankAccount> intermediaryAgentsIterator = transaction.getIntermediaryAgents().iterator();
        if (intermediaryAgentsIterator.hasNext()) {
            BankAccount first = intermediaryAgentsIterator.next();
            optionalBranchAndFinancialInstitutionIdentificationOpt(first).ifPresent(creditTransferTransactionInformation::setIntrmyAgt1);
        }

        return creditTransferTransactionInformation;
    }

    private Optional<BranchAndFinancialInstitutionIdentification4CH> optionalBranchAndFinancialInstitutionIdentificationOpt(BankAccount bankAccount) {
        return bankAccount.getBic().map(bic -> {
            BicUtil.validate(bic);
            FinancialInstitutionIdentification7CH financialInstitutionIdentification = new FinancialInstitutionIdentification7CH();
            financialInstitutionIdentification.setBIC(bic);
            BranchAndFinancialInstitutionIdentification4CH branchAndFinancialInstitutionIdentification = new BranchAndFinancialInstitutionIdentification4CH();
            branchAndFinancialInstitutionIdentification.setFinInstnId(financialInstitutionIdentification);
            return branchAndFinancialInstitutionIdentification;
        });
    }

    private BranchAndFinancialInstitutionIdentification4CHBicOrClrId mandatoryBranchAndFinancialInstitutionIdentification(BankAccount bankAccount) {
        BranchAndFinancialInstitutionIdentification4CHBicOrClrId branchAndFinancialInstitutionIdentification = new BranchAndFinancialInstitutionIdentification4CHBicOrClrId();
        FinancialInstitutionIdentification7CHBicOrClrId financialInstitutionIdentification = new FinancialInstitutionIdentification7CHBicOrClrId();
        bankAccount.getBic().ifPresent(bic -> {
            BicUtil.validate(bic);
            financialInstitutionIdentification.setBIC(bic);
        });
        branchAndFinancialInstitutionIdentification.setFinInstnId(financialInstitutionIdentification);
        return branchAndFinancialInstitutionIdentification;
    }

    private PartyIdentification32CH partyIdentification(Party party) {
        PartyIdentification32CH partyIdentification = new PartyIdentification32CH();
        if (party != null) {
            party.getName().ifPresent(partyIdentification::setNm);
            party.getPostalAddress().map(this::postalAddress).ifPresent(partyIdentification::setPstlAdr);
        }
        return partyIdentification;
    }

    private PartyIdentification32CHNameAndId partyIdentificationHeader(Party party) {
        PartyIdentification32CHNameAndId partyIdentification = new PartyIdentification32CHNameAndId();
        if (party != null) {
            party.getName().ifPresent(partyIdentification::setNm);
        }
        return partyIdentification;
    }

    private PartyIdentification32CHName partyIdentificationName(Party party) {
        if (party == null) {
            return null;
        }
        PartyIdentification32CHName partyIdentification = new PartyIdentification32CHName();
        partyIdentification.setNm(party.getName()
                .orElseThrow(() -> new IllegalArgumentException("Party name is required for pain.001.001.03.ch.02")));
        party.getPostalAddress().map(this::postalAddress).ifPresent(partyIdentification::setPstlAdr);
        return partyIdentification;
    }

    private PostalAddress6CH postalAddress(PostalAddress postalAddress) {
        PostalAddress6CH postalAddress6 = new PostalAddress6CH();
        postalAddress.getType().ifPresent(typeCode -> postalAddress6.setAdrTp(AddressType2Code.fromValue(typeCode)));
        postalAddress.getDepartment().ifPresent(postalAddress6::setDept);
        postalAddress.getSubDepartment().ifPresent(postalAddress6::setSubDept);
        postalAddress.getStreetName().ifPresent(postalAddress6::setStrtNm);
        postalAddress.getBuildingNumber().ifPresent(postalAddress6::setBldgNb);
        postalAddress.getTownName().ifPresent(postalAddress6::setTwnNm);
        postalAddress.getPostCode().ifPresent(postalAddress6::setPstCd);
        postalAddress.getCountrySubDivision().ifPresent(postalAddress6::setCtrySubDvsn);
        postalAddress.getCountry().ifPresent(postalAddress6::setCtry);
        int lines = 0;
        for (String addressLine : postalAddress.getAddressLines()) {
            if (lines >= 2) {
                break;
            }
            postalAddress6.getAdrLine().add(addressLine);
            lines++;
        }
        return postalAddress6;
    }

    private CashAccount16CHIdTpCcy debtorCashAccount(BankAccount bankAccount) {
        CashAccount16CHIdTpCcy cashAccount = new CashAccount16CHIdTpCcy();
        cashAccount.setId(accountIdentification(bankAccount));
        return cashAccount;
    }

    private CashAccount16CHId creditorCashAccount(BankAccount bankAccount) {
        CashAccount16CHId cashAccount = new CashAccount16CHId();
        cashAccount.setId(accountIdentification(bankAccount));
        return cashAccount;
    }

    private AccountIdentification4ChoiceCH accountIdentification(BankAccount bankAccount) {
        AccountIdentification4ChoiceCH accountIdentification = new AccountIdentification4ChoiceCH();
        Optional<String> optionalIban = bankAccount.getIban();
        Optional<String> optionalOtherId = bankAccount.getOtherId();
        if (optionalIban.isPresent()) {
            String iban = optionalIban.get();
            IbanUtil.validate(iban);
            accountIdentification.setIBAN(iban);
        } else if (optionalOtherId.isPresent()) {
            GenericAccountIdentification1CH genericAccountIdentification = new GenericAccountIdentification1CH();
            genericAccountIdentification.setId(optionalOtherId.get());
            accountIdentification.setOthr(genericAccountIdentification);
        } else {
            throw new IllegalArgumentException("IBAN or otherId must be provided");
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof JAXBCreditTransferV03Ch02)) return false;
        JAXBCreditTransferV03Ch02 that = (JAXBCreditTransferV03Ch02) o;
        return batchBooking == that.batchBooking && instructionPriority == that.instructionPriority && Objects.equals(serviceLevelCode, that.serviceLevelCode) && Objects.equals(debtor, that.debtor) && Objects.equals(debtorAccount, that.debtorAccount) && Objects.equals(transactions, that.transactions) && Objects.equals(id, that.id) && Objects.equals(creationDateTime, that.creationDateTime) && Objects.equals(requestedExecutionDate, that.requestedExecutionDate) && chargeBearer == that.chargeBearer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(instructionPriority, serviceLevelCode, debtor, debtorAccount, transactions, id, creationDateTime, requestedExecutionDate, chargeBearer, batchBooking);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", JAXBCreditTransferV03Ch02.class.getSimpleName() + "[", "]")
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
