package io.inisos.bank4j.impl;

import io.inisos.bank4j.*;
import iso.std.iso._20022.tech.xsd.pain_001_001.*;
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
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * A JAXB ISO 20022 Credit Transfer with PAIN.001.001.03
 *
 * @author Patrice Blanchardie
 */
public class JAXBCreditTransfer implements CreditTransferOperation {

    private static final DateTimeFormatter FORMAT_AS_ID = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");

    private final Priority instructionPriority;
    private final String serviceLevelCode;
    private final Party debtor;
    private final BankAccount debtorAccount;
    private final Collection<Transaction> transactions;
    private final String id;
    private final LocalDateTime creationDateTime;
    private final LocalDate requestedExecutionDate;
    private final ChargeBearerType1Code chargeBearerCode;
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
     * @param requestedExecutionDate optional requested execution date and time, defaults to tomorrow
     * @param chargeBearerCode       optional charge bearer code defines who is bearing the charges of the transfer, by default it is set to 'SLEV' (Service Level)
     */
    public JAXBCreditTransfer(Priority instructionPriority, String serviceLevelCode, Party debtor, BankAccount debtorAccount, Collection<Transaction> transactions, String id, LocalDateTime creationDateTime, LocalDate requestedExecutionDate, ChargeBearerType1Code chargeBearerCode, Boolean batchBooking) {
        this.instructionPriority = instructionPriority;
        this.serviceLevelCode = serviceLevelCode;
        this.debtor = debtor;
        this.debtorAccount = Objects.requireNonNull(debtorAccount, "Debtor account cannot be null");
        this.transactions = requireTransaction(Objects.requireNonNull(transactions));
        this.creationDateTime = Optional.ofNullable(creationDateTime).orElse(LocalDateTime.now());
        this.requestedExecutionDate = Optional.ofNullable(requestedExecutionDate).orElse(LocalDate.now().plusDays(1));
        this.id = Optional.ofNullable(id).orElseGet(() -> FORMAT_AS_ID.format(this.creationDateTime));
        this.chargeBearerCode = Optional.ofNullable(chargeBearerCode).orElseGet(() -> ChargeBearerType1Code.SLEV);
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
                new QName("urn:iso:std:iso:20022:tech:xsd:pain.001.001.03", "Document"),
                Document.class,
                document);
    }

    private CustomerCreditTransferInitiationV03 build() {

        CustomerCreditTransferInitiationV03 cti = new CustomerCreditTransferInitiationV03();

        cti.setGrpHdr(header());

        PaymentInstructionInformation3 paymentInstructionInformationSCT3 = new PaymentInstructionInformation3();
        paymentInstructionInformationSCT3.setPmtInfId(this.id);
        paymentInstructionInformationSCT3.setPmtMtd(PaymentMethod3Code.TRF);
        paymentInstructionInformationSCT3.setBtchBookg(this.batchBooking);
        paymentInstructionInformationSCT3.setNbOfTxs(String.valueOf(this.transactions.size()));
        paymentInstructionInformationSCT3.setCtrlSum(this.getTotalAmount());
        paymentInstructionInformationSCT3.setDbtr(partyIdentification(this.debtor));
        paymentInstructionInformationSCT3.setDbtrAcct(cashAccount(this.debtorAccount));
        paymentInstructionInformationSCT3.setDbtrAgt(mandatoryBranchAndFinancialInstitutionIdentification(this.debtorAccount));

        PaymentTypeInformation19 paymentTypeInformation = new PaymentTypeInformation19();
        if (this.instructionPriority != null) {
            paymentTypeInformation.setInstrPrty(Priority2Code.fromValue(this.instructionPriority.name()));
        }
        if (this.serviceLevelCode != null) {
            ServiceLevel8Choice serviceLevel = new ServiceLevel8Choice();
            serviceLevel.setCd(this.serviceLevelCode);
            paymentTypeInformation.setSvcLvl(serviceLevel);
        }
        paymentInstructionInformationSCT3.setPmtTpInf(paymentTypeInformation);

        paymentInstructionInformationSCT3.setReqdExctnDt(this.datatypeFactory.newXMLGregorianCalendar(DateTimeFormatter.ISO_LOCAL_DATE.format(requestedExecutionDate)));

        paymentInstructionInformationSCT3.setChrgBr(this.chargeBearerCode);

        for (Transaction transaction : this.transactions) {
            paymentInstructionInformationSCT3.getCdtTrfTxInf().add(transaction(transaction));
        }

        cti.getPmtInf().add(paymentInstructionInformationSCT3);

        return cti;
    }

    private GroupHeader32 header() {
        GroupHeader32 head = new GroupHeader32();
        head.setMsgId(id);
        head.setCreDtTm(this.datatypeFactory.newXMLGregorianCalendar(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(creationDateTime)));
        head.setNbOfTxs(String.valueOf(this.transactions.size()));
        head.setCtrlSum(this.getTotalAmount());
        head.setInitgPty(partyIdentification(this.debtor));
        return head;
    }

    private CreditTransferTransactionInformation10 transaction(Transaction transaction) {

        // payment identification
        PaymentIdentification1 paymentIdentificationSEPA = new PaymentIdentification1();
        paymentIdentificationSEPA.setEndToEndId(transaction.getEndToEndId());
        transaction.getId().ifPresent(paymentIdentificationSEPA::setInstrId);

        // amount
        ActiveOrHistoricCurrencyAndAmount activeOrHistoricCurrencyAndAmount = new ActiveOrHistoricCurrencyAndAmount();
        activeOrHistoricCurrencyAndAmount.setCcy(transaction.getCurrencyCode());
        activeOrHistoricCurrencyAndAmount.setValue(transaction.getAmount());
        AmountType3Choice amountType = new AmountType3Choice();
        amountType.setInstdAmt(activeOrHistoricCurrencyAndAmount);

        // transaction
        CreditTransferTransactionInformation10 creditTransferTransactionInformation = new CreditTransferTransactionInformation10();
        creditTransferTransactionInformation.setPmtId(paymentIdentificationSEPA);
        creditTransferTransactionInformation.setAmt(amountType);
        creditTransferTransactionInformation.setCdtr(partyIdentification(transaction.getParty().orElse(null)));
        creditTransferTransactionInformation.setCdtrAcct(cashAccount(transaction.getAccount()));

        // remittance information
        if (transaction.getRemittanceInformationUnstructured().size() > 0) {
            RemittanceInformation5 remittanceInformation = new RemittanceInformation5();
            remittanceInformation.getUstrd().addAll(transaction.getRemittanceInformationUnstructured());
            creditTransferTransactionInformation.setRmtInf(remittanceInformation);
        }

        transaction.getChargeBearerCode().ifPresent(creditTransferTransactionInformation::setChrgBr);

        optionalBranchAndFinancialInstitutionIdentificationOpt(transaction.getAccount()).ifPresent(creditTransferTransactionInformation::setCdtrAgt);
        Iterator<BankAccount> intermediaryAgentsIterator = transaction.getIntermediaryAgents().iterator();
        if (intermediaryAgentsIterator.hasNext()) {
            BankAccount first = intermediaryAgentsIterator.next();
            creditTransferTransactionInformation.setIntrmyAgt1Acct(cashAccount(first));
            optionalBranchAndFinancialInstitutionIdentificationOpt(first).ifPresent(creditTransferTransactionInformation::setIntrmyAgt1);
        }
        if (intermediaryAgentsIterator.hasNext()) {
            BankAccount second = intermediaryAgentsIterator.next();
            creditTransferTransactionInformation.setIntrmyAgt2Acct(cashAccount(second));
            optionalBranchAndFinancialInstitutionIdentificationOpt(second).ifPresent(creditTransferTransactionInformation::setIntrmyAgt2);
        }
        if (intermediaryAgentsIterator.hasNext()) {
            BankAccount third = intermediaryAgentsIterator.next();
            creditTransferTransactionInformation.setIntrmyAgt3Acct(cashAccount(third));
            optionalBranchAndFinancialInstitutionIdentificationOpt(third).ifPresent(creditTransferTransactionInformation::setIntrmyAgt3);
        }

        return creditTransferTransactionInformation;
    }

    private Optional<BranchAndFinancialInstitutionIdentification4> optionalBranchAndFinancialInstitutionIdentificationOpt(BankAccount bankAccount) {
        return bankAccount.getBic().map(bic -> {
            BicUtil.validate(bic);
            FinancialInstitutionIdentification7 financialInstitutionIdentification = new FinancialInstitutionIdentification7();
            financialInstitutionIdentification.setBIC(bic);
            BranchAndFinancialInstitutionIdentification4 branchAndFinancialInstitutionIdentification = new BranchAndFinancialInstitutionIdentification4();
            branchAndFinancialInstitutionIdentification.setFinInstnId(financialInstitutionIdentification);
            return branchAndFinancialInstitutionIdentification;
        });
    }

    private BranchAndFinancialInstitutionIdentification4 mandatoryBranchAndFinancialInstitutionIdentification(BankAccount bankAccount) {
        BranchAndFinancialInstitutionIdentification4 branchAndFinancialInstitutionIdentification = new BranchAndFinancialInstitutionIdentification4();
        FinancialInstitutionIdentification7 financialInstitutionIdentification = new FinancialInstitutionIdentification7();
        bankAccount.getBic().ifPresent(bic -> {
            BicUtil.validate(bic);
            financialInstitutionIdentification.setBIC(bic);
        });
        branchAndFinancialInstitutionIdentification.setFinInstnId(financialInstitutionIdentification);
        return branchAndFinancialInstitutionIdentification;
    }

    private PartyIdentification32 partyIdentification(Party party) {
        PartyIdentification32 partyIdentification = new PartyIdentification32();
        if (party != null) {
            party.getName().ifPresent(partyIdentification::setNm);
            party.getPostalAddress().map(this::postalAddress).ifPresent(partyIdentification::setPstlAdr);
        }
        return partyIdentification;
    }

    private PostalAddress6 postalAddress(PostalAddress postalAddress) {
        PostalAddress6 postalAddress6 = new PostalAddress6();
        postalAddress.getType().ifPresent(typeCode -> postalAddress6.setAdrTp(AddressType2Code.fromValue(typeCode)));
        postalAddress.getDepartment().ifPresent(postalAddress6::setDept);
        postalAddress.getSubDepartment().ifPresent(postalAddress6::setSubDept);
        postalAddress.getStreetName().ifPresent(postalAddress6::setStrtNm);
        postalAddress.getBuildingNumber().ifPresent(postalAddress6::setBldgNb);
        postalAddress.getTownName().ifPresent(postalAddress6::setTwnNm);
        postalAddress.getPostCode().ifPresent(postalAddress6::setPstCd);
        postalAddress.getCountrySubDivision().ifPresent(postalAddress6::setCtrySubDvsn);
        postalAddress.getCountry().ifPresent(postalAddress6::setCtry);
        postalAddress.getAddressLines().forEach(addressLine -> postalAddress6.getAdrLine().add(addressLine));
        return postalAddress6;
    }

    private CashAccount16 cashAccount(BankAccount bankAccount) {
        CashAccount16 cashAccount = new CashAccount16();
        cashAccount.setId(accountIdentification(bankAccount));
        bankAccount.getName().ifPresent(cashAccount::setNm);
        return cashAccount;
    }

    private AccountIdentification4Choice accountIdentification(BankAccount bankAccount) {
        AccountIdentification4Choice accountIdentification = new AccountIdentification4Choice();
        Optional<String> optionalIban = bankAccount.getIban();
        Optional<String> optionalOtherId = bankAccount.getOtherId();
        if (optionalIban.isPresent()) {
            String iban = optionalIban.get();
            IbanUtil.validate(iban);
            accountIdentification.setIBAN(iban);
        } else if (optionalOtherId.isPresent()) {
            GenericAccountIdentification1 genericAccountIdentification = new GenericAccountIdentification1();
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
        if (!(o instanceof JAXBCreditTransfer)) return false;
        JAXBCreditTransfer that = (JAXBCreditTransfer) o;
        return serviceLevelCode.equals(that.serviceLevelCode) && debtor.equals(that.debtor) && getTransactions().equals(that.getTransactions()) && id.equals(that.id) && creationDateTime.equals(that.creationDateTime) && requestedExecutionDate.equals(that.requestedExecutionDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceLevelCode, debtor, getTransactions(), id, creationDateTime, requestedExecutionDate);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", JAXBCreditTransfer.class.getSimpleName() + "[", "]")
                .add("serviceLevelCode='" + serviceLevelCode + "'")
                .add("debtor=" + debtor)
                .add("id='" + id + "'")
                .add("creationDateTime=" + creationDateTime)
                .add("requestedExecutionDate=" + requestedExecutionDate)
                .toString();
    }
}
