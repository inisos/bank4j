package io.inisos.bank4j.impl;

import io.inisos.bank4j.BankAccount;
import io.inisos.bank4j.CreditTransfer;
import io.inisos.bank4j.Transaction;
import iso.std.iso._20022.tech.xsd.pain_001_001.*;
import org.iban4j.BicUtil;
import org.iban4j.IbanUtil;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * A JAXB ISO 20022 Credit Transfer with PAIN.001.001.03
 *
 * @author Patrice Blanchardie
 */
public class JAXBCreditTransfer implements CreditTransfer {

    private static final DateTimeFormatter FORMAT_AS_ID = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");

    private final String serviceLevelCode;
    private final BankAccount debtor;
    private final Collection<Transaction> transactions;
    private final String id;
    private final LocalDateTime executionDate;

    private final DatatypeFactory datatypeFactory;

    private final CustomerCreditTransferInitiationV03 customerCreditTransferInitiation;

    /**
     * Constructor
     *
     * @param serviceLevelCode eg. "SEPA"
     * @param debtor           debtor account
     * @param transactions     transactions (cannot contain duplicates)
     * @param id               optional identifier, defaults to execution date and time
     * @param dateTime         optional execution date and time, defaults to now
     */
    public JAXBCreditTransfer(String serviceLevelCode, BankAccount debtor, Collection<Transaction> transactions, String id, LocalDateTime dateTime) {
        this.serviceLevelCode = Objects.requireNonNull(serviceLevelCode);
        this.debtor = Objects.requireNonNull(debtor);
        this.transactions = Objects.requireNonNull(transactions);
        this.executionDate = Optional.ofNullable(dateTime).orElse(LocalDateTime.now());
        this.id = Optional.ofNullable(id).orElseGet(() -> FORMAT_AS_ID.format(executionDate));

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
        paymentInstructionInformationSCT3.setBtchBookg(false);
        paymentInstructionInformationSCT3.setNbOfTxs(String.valueOf(this.transactions.size()));
        paymentInstructionInformationSCT3.setCtrlSum(this.getTotalAmount());
        paymentInstructionInformationSCT3.setDbtr(partyIdentification(this.debtor));
        paymentInstructionInformationSCT3.setDbtrAcct(cashAccount(this.debtor));
        paymentInstructionInformationSCT3.setDbtrAgt(branchAndFinancialInstitutionIdentification(this.debtor));

        ServiceLevel8Choice serviceLevel = new ServiceLevel8Choice();
        serviceLevel.setCd(this.serviceLevelCode);
        PaymentTypeInformation19 paymentTypeInformation = new PaymentTypeInformation19();
        paymentTypeInformation.setSvcLvl(serviceLevel);
        paymentInstructionInformationSCT3.setPmtTpInf(paymentTypeInformation);

        paymentInstructionInformationSCT3.setChrgBr(ChargeBearerType1Code.SLEV);

        for (Transaction transaction : this.transactions) {
            paymentInstructionInformationSCT3.getCdtTrfTxInf().add(transaction(transaction));
        }

        cti.getPmtInf().add(paymentInstructionInformationSCT3);

        return cti;
    }

    private GroupHeader32 header() {
        GroupHeader32 head = new GroupHeader32();
        head.setMsgId(id);
        head.setCreDtTm(this.datatypeFactory.newXMLGregorianCalendar(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(executionDate)));
        head.setNbOfTxs(String.valueOf(this.transactions.size()));
        head.setCtrlSum(this.getTotalAmount());
        head.setInitgPty(partyIdentification(this.debtor));
        return head;
    }

    private CreditTransferTransactionInformation10 transaction(Transaction transaction) {

        // payment identification
        PaymentIdentification1 paymentIdentificationSEPA = new PaymentIdentification1();
        paymentIdentificationSEPA.setEndToEndId(transaction.getEndToEndId());
        if (transaction.getId() != null) {
            paymentIdentificationSEPA.setInstrId(transaction.getId());
        }

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
        creditTransferTransactionInformation.setCdtr(partyIdentification(transaction.getThirdParty()));
        creditTransferTransactionInformation.setCdtrAcct(cashAccount(transaction.getThirdParty()));
        creditTransferTransactionInformation.setCdtrAgt(branchAndFinancialInstitutionIdentification(transaction.getThirdParty()));

        return creditTransferTransactionInformation;
    }

    private BranchAndFinancialInstitutionIdentification4 branchAndFinancialInstitutionIdentification(BankAccount bankAccount) {
        if (bankAccount.getBic() == null) {
            return null;
        }
        BicUtil.validate(bankAccount.getBic());
        FinancialInstitutionIdentification7 financialInstitutionIdentification = new FinancialInstitutionIdentification7();
        financialInstitutionIdentification.setBIC(bankAccount.getBic());
        BranchAndFinancialInstitutionIdentification4 branchAndFinancialInstitutionIdentification = new BranchAndFinancialInstitutionIdentification4();
        branchAndFinancialInstitutionIdentification.setFinInstnId(financialInstitutionIdentification);
        return branchAndFinancialInstitutionIdentification;
    }

    private PartyIdentification32 partyIdentification(BankAccount bankAccount) {
        PartyIdentification32 partyIdentification = new PartyIdentification32();
        partyIdentification.setNm(bankAccount.getName());
        return partyIdentification;
    }

    private CashAccount16 cashAccount(BankAccount bankAccount) {
        CashAccount16 cashAccount = new CashAccount16();
        cashAccount.setId(accountIdentification(bankAccount));
        return cashAccount;
    }

    private AccountIdentification4Choice accountIdentification(BankAccount bankAccount) {
        IbanUtil.validate(bankAccount.getIban());
        AccountIdentification4Choice accountIdentification = new AccountIdentification4Choice();
        accountIdentification.setIBAN(bankAccount.getIban());
        return accountIdentification;
    }

    @Override
    public BankAccount getDebtor() {
        return debtor;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public LocalDateTime getExecutionDate() {
        return executionDate;
    }

    @Override
    public Collection<Transaction> getTransactions() {
        return transactions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JAXBCreditTransfer)) return false;
        JAXBCreditTransfer that = (JAXBCreditTransfer) o;
        return serviceLevelCode.equals(that.serviceLevelCode) && debtor.equals(that.debtor) && getTransactions().equals(that.getTransactions()) && id.equals(that.id) && executionDate.equals(that.executionDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceLevelCode, debtor, getTransactions(), id, executionDate);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", JAXBCreditTransfer.class.getSimpleName() + "[", "]")
                .add("serviceLevelCode='" + serviceLevelCode + "'")
                .add("debtor=" + debtor)
                .add("id='" + id + "'")
                .add("executionDate=" + executionDate)
                .toString();
    }
}
