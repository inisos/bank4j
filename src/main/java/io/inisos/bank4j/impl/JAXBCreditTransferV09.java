package io.inisos.bank4j.impl;

import io.inisos.bank4j.*;
import iso._20022.pain_001_001_09.*;
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
import java.util.*;

/**
 * A JAXB ISO 20022 Credit Transfer with PAIN.001.001.09
 *
 * @author Patrice Blanchardie
 */
public class JAXBCreditTransferV09 implements CreditTransferOperation {

    private static final DateTimeFormatter FORMAT_AS_ID = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");

    private final Priority instructionPriority;
    private final String serviceLevelCode;
    private final Party debtor;
    private final BankAccount debtorAccount;
    private final Collection<Transaction> transactions;
    private final String id;
    private final LocalDateTime creationDateTime;
    private final LocalDate requestedExecutionDate;
    private final ZonedDateTime requestedExecutionDateTime;
    private final ChargeBearer chargeBearer;
    private final boolean batchBooking;
    private final Boolean instantPayment;

    private final DatatypeFactory datatypeFactory;

    private final CustomerCreditTransferInitiationV09 customerCreditTransferInitiation;

    /**
     * Constructor
     *
     * @param instructionPriority        optional priority
     * @param serviceLevelCode           optional e.g. "SEPA"
     * @param debtor                     optional debtor
     * @param debtorAccount              debtor account
     * @param transactions               transactions (cannot contain duplicates)
     * @param id                         optional identifier, defaults to execution date and time
     * @param creationDateTime           optional message creation date and time, defaults to now
     * @param requestedExecutionDate     optional requested execution date, defaults to tomorrow
     * @param requestedExecutionDateTime optional requested execution date and time
     * @param chargeBearer               optional charge bearer code defines who is bearing the charges of the transfer, by default it is set to 'SLEV' (Service Level)
     */
    public JAXBCreditTransferV09(Priority instructionPriority,
                                 String serviceLevelCode,
                                 Party debtor,
                                 BankAccount debtorAccount,
                                 Collection<Transaction> transactions,
                                 String id,
                                 LocalDateTime creationDateTime,
                                 LocalDate requestedExecutionDate,
                                 ZonedDateTime requestedExecutionDateTime,
                                 ChargeBearer chargeBearer,
                                 Boolean batchBooking,
                                 Boolean instantPayment) {
        this.instructionPriority = instructionPriority;
        this.serviceLevelCode = serviceLevelCode;
        this.debtor = debtor;
        this.debtorAccount = Objects.requireNonNull(debtorAccount, "Debtor account cannot be null");
        this.transactions = requireTransaction(Objects.requireNonNull(transactions));
        this.creationDateTime = Optional.ofNullable(creationDateTime).orElse(LocalDateTime.now());
        this.instantPayment = instantPayment;
        if(requestedExecutionDate != null && requestedExecutionDateTime != null) {
            throw new IllegalArgumentException("You cannot specify both requestedExecutionDate and requestedExecutionDateTime");
        }
        this.requestedExecutionDateTime = requestedExecutionDateTime;
        if(this.requestedExecutionDateTime == null) {
            this.requestedExecutionDate = Optional.ofNullable(requestedExecutionDate).orElse(LocalDate.now().plusDays(1));
        } else {
            this.requestedExecutionDate = null;
        }
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
                new QName("urn:iso:std:iso:20022:tech:xsd:pain.001.001.09", "Document"),
                Document.class,
                document);
    }

    private CustomerCreditTransferInitiationV09 build() {

        CustomerCreditTransferInitiationV09 cti = new CustomerCreditTransferInitiationV09();

        cti.setGrpHdr(header());

        PaymentInstruction30 paymentInstructionInformationSCT3 = new PaymentInstruction30();
        paymentInstructionInformationSCT3.setPmtInfId(this.id);
        paymentInstructionInformationSCT3.setPmtMtd(PaymentMethod3Code.TRF);
        paymentInstructionInformationSCT3.setBtchBookg(this.batchBooking);
        paymentInstructionInformationSCT3.setNbOfTxs(String.valueOf(this.transactions.size()));
        paymentInstructionInformationSCT3.setCtrlSum(this.getTotalAmount());
        paymentInstructionInformationSCT3.setDbtr(partyIdentification(this.debtor));
        paymentInstructionInformationSCT3.setDbtrAcct(cashAccount(this.debtorAccount));
        paymentInstructionInformationSCT3.setDbtrAgt(mandatoryBranchAndFinancialInstitutionIdentification(this.debtorAccount));

        PaymentTypeInformation26 paymentTypeInformation = new PaymentTypeInformation26();
        if (this.instructionPriority != null) {
            paymentTypeInformation.setInstrPrty(Priority2Code.fromValue(this.instructionPriority.name()));
        }
        if (this.serviceLevelCode != null) {
            ServiceLevel8Choice serviceLevel = new ServiceLevel8Choice();
            serviceLevel.setCd(this.serviceLevelCode);
            paymentTypeInformation.getSvcLvl().add(serviceLevel);
        }
        if (this.instantPayment != null && this.instantPayment) {
            paymentTypeInformation.setLclInstrm(new LocalInstrument2Choice());
            paymentTypeInformation.getLclInstrm().setCd("INST");
        }

        paymentInstructionInformationSCT3.setPmtTpInf(paymentTypeInformation);

        DateAndDateTime2Choice dateAndDateTime2Choice = new DateAndDateTime2Choice();
        if(requestedExecutionDate != null) {
            dateAndDateTime2Choice.setDt(this.datatypeFactory.newXMLGregorianCalendar(DateTimeFormatter.ISO_LOCAL_DATE.format(requestedExecutionDate)));
        } else if(requestedExecutionDateTime != null) {
            dateAndDateTime2Choice.setDtTm(this.datatypeFactory.newXMLGregorianCalendar(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(requestedExecutionDateTime)));
        } else {
            throw new IllegalArgumentException("Either requestedExecutionDate or requestedExecutionDateTime must be set");
        }
        paymentInstructionInformationSCT3.setReqdExctnDt(dateAndDateTime2Choice);

        paymentInstructionInformationSCT3.setChrgBr(ChargeBearerType1Code.fromValue(this.chargeBearer.name()));

        for (Transaction transaction : this.transactions) {
            paymentInstructionInformationSCT3.getCdtTrfTxInf().add(transaction(transaction));
        }

        cti.getPmtInf().add(paymentInstructionInformationSCT3);

        return cti;
    }

    private GroupHeader85 header() {
        GroupHeader85 head = new GroupHeader85();
        head.setMsgId(id);
        head.setCreDtTm(this.datatypeFactory.newXMLGregorianCalendar(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(creationDateTime)));
        head.setNbOfTxs(String.valueOf(this.transactions.size()));
        head.setCtrlSum(this.getTotalAmount());
        head.setInitgPty(partyIdentification(this.debtor));
        return head;
    }

    private CreditTransferTransaction34 transaction(Transaction transaction) {

        // payment identification
        PaymentIdentification6 paymentIdentificationSEPA = new PaymentIdentification6();
        paymentIdentificationSEPA.setEndToEndId(transaction.getEndToEndId());
        transaction.getId().ifPresent(paymentIdentificationSEPA::setInstrId);

        // amount
        ActiveOrHistoricCurrencyAndAmount activeOrHistoricCurrencyAndAmount = new ActiveOrHistoricCurrencyAndAmount();
        activeOrHistoricCurrencyAndAmount.setCcy(transaction.getCurrencyCode());
        activeOrHistoricCurrencyAndAmount.setValue(transaction.getAmount());
        AmountType4Choice amountType = new AmountType4Choice();
        amountType.setInstdAmt(activeOrHistoricCurrencyAndAmount);

        // transaction
        CreditTransferTransaction34 creditTransferTransactionInformation = new CreditTransferTransaction34();
        creditTransferTransactionInformation.setPmtId(paymentIdentificationSEPA);
        creditTransferTransactionInformation.setAmt(amountType);
        creditTransferTransactionInformation.setCdtr(partyIdentification(transaction.getParty().orElse(null)));
        creditTransferTransactionInformation.setCdtrAcct(cashAccount(transaction.getAccount()));

        // remittance information
        if (transaction.getRemittanceInformationUnstructured().size() > 0) {
            RemittanceInformation16 remittanceInformation = new RemittanceInformation16();
            remittanceInformation.getUstrd().addAll(transaction.getRemittanceInformationUnstructured());
            creditTransferTransactionInformation.setRmtInf(remittanceInformation);
        }

        transaction.getChargeBearer()
                .map(cb -> ChargeBearerType1Code.fromValue(cb.name()))
                .ifPresent(creditTransferTransactionInformation::setChrgBr);

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

    private Optional<BranchAndFinancialInstitutionIdentification6> optionalBranchAndFinancialInstitutionIdentificationOpt(BankAccount bankAccount) {
        return bankAccount.getBic().map(bic -> {
            BicUtil.validate(bic);
            FinancialInstitutionIdentification18 financialInstitutionIdentification = new FinancialInstitutionIdentification18();
            financialInstitutionIdentification.setBICFI(bic);
            BranchAndFinancialInstitutionIdentification6 branchAndFinancialInstitutionIdentification = new BranchAndFinancialInstitutionIdentification6();
            branchAndFinancialInstitutionIdentification.setFinInstnId(financialInstitutionIdentification);
            return branchAndFinancialInstitutionIdentification;
        });
    }

    private BranchAndFinancialInstitutionIdentification6 mandatoryBranchAndFinancialInstitutionIdentification(BankAccount bankAccount) {
        BranchAndFinancialInstitutionIdentification6 branchAndFinancialInstitutionIdentification = new BranchAndFinancialInstitutionIdentification6();
        FinancialInstitutionIdentification18 financialInstitutionIdentification = new FinancialInstitutionIdentification18();
        bankAccount.getBic().ifPresentOrElse(bic -> {
            BicUtil.validate(bic);
            financialInstitutionIdentification.setBICFI(bic);
        }, () -> {
            throw new IllegalArgumentException("BIC must be provided for debtor account");
        });
        branchAndFinancialInstitutionIdentification.setFinInstnId(financialInstitutionIdentification);
        return branchAndFinancialInstitutionIdentification;
    }

    private PartyIdentification135 partyIdentification(Party party) {
        PartyIdentification135 partyIdentification = new PartyIdentification135();
        if (party != null) {
            party.getName().ifPresent(partyIdentification::setNm);
            party.getPostalAddress().map(this::postalAddress).ifPresent(partyIdentification::setPstlAdr);
        }
        return partyIdentification;
    }

    private PostalAddress24 postalAddress(PostalAddress postalAddress) {
        PostalAddress24 postalAddress24 = new PostalAddress24();

        postalAddress.getType()
                .map(AddressType2Code::fromValue)
                .map(addressType2Code -> {
                    AddressType3Choice addressType3Choice = new AddressType3Choice();
                    addressType3Choice.setCd(addressType2Code);
                    return addressType3Choice;
                })
                .ifPresent(postalAddress24::setAdrTp);
        postalAddress.getDepartment().ifPresent(postalAddress24::setDept);
        postalAddress.getSubDepartment().ifPresent(postalAddress24::setSubDept);
        postalAddress.getStreetName().ifPresent(postalAddress24::setStrtNm);
        postalAddress.getBuildingNumber().ifPresent(postalAddress24::setBldgNb);
        postalAddress.getTownName().ifPresent(postalAddress24::setTwnNm);
        postalAddress.getPostCode().ifPresent(postalAddress24::setPstCd);
        postalAddress.getCountrySubDivision().ifPresent(postalAddress24::setCtrySubDvsn);
        postalAddress.getCountry().ifPresent(postalAddress24::setCtry);
        postalAddress.getAddressLines().forEach(addressLine -> postalAddress24.getAdrLine().add(addressLine));
        return postalAddress24;
    }

    private CashAccount38 cashAccount(BankAccount bankAccount) {
        CashAccount38 cashAccount = new CashAccount38();
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
        return requestedExecutionDateTime;
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
        if (!(o instanceof JAXBCreditTransferV09)) return false;
        JAXBCreditTransferV09 that = (JAXBCreditTransferV09) o;
        return batchBooking == that.batchBooking && instructionPriority == that.instructionPriority && Objects.equals(serviceLevelCode, that.serviceLevelCode) && Objects.equals(debtor, that.debtor) && Objects.equals(debtorAccount, that.debtorAccount) && Objects.equals(transactions, that.transactions) && Objects.equals(id, that.id) && Objects.equals(creationDateTime, that.creationDateTime) && Objects.equals(requestedExecutionDate, that.requestedExecutionDate) && Objects.equals(requestedExecutionDateTime, that.requestedExecutionDateTime) && chargeBearer == that.chargeBearer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(instructionPriority, serviceLevelCode, debtor, debtorAccount, transactions, id, creationDateTime, requestedExecutionDate, requestedExecutionDateTime, chargeBearer, batchBooking);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", JAXBCreditTransferV09.class.getSimpleName() + "[", "]")
                .add("instructionPriority=" + instructionPriority)
                .add("serviceLevelCode='" + serviceLevelCode + "'")
                .add("debtor=" + debtor)
                .add("debtorAccount=" + debtorAccount)
                .add("id='" + id + "'")
                .add("creationDateTime=" + creationDateTime)
                .add("requestedExecutionDate=" + requestedExecutionDate)
                .add("requestedExecutionDateTime=" + requestedExecutionDateTime)
                .add("chargeBearer=" + chargeBearer)
                .add("batchBooking=" + batchBooking)
                .toString();
    }
}
