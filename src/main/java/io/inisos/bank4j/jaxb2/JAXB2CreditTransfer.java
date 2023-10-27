package io.inisos.bank4j.jaxb2;

import io.inisos.bank4j.*;
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
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * A JAXB ISO 20022 Credit Transfer with PAIN.001.001.03
 *
 * @author Patrice Blanchardie
 */
public class JAXB2CreditTransfer implements Message {

    private final CreditTransfer creditTransfer;

    private final DatatypeFactory datatypeFactory;

    private final CustomerCreditTransferInitiationV03 customerCreditTransferInitiation;

    public JAXB2CreditTransfer(CreditTransfer creditTransfer) {
        this.creditTransfer = requireNonNull(creditTransfer, "Credit transfer cannot be null");

        try {
            this.datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new JAXB2Exception(e);
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
            throw new JAXB2Exception(e);
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
        paymentInstructionInformationSCT3.setPmtInfId(this.creditTransfer.getId());
        paymentInstructionInformationSCT3.setPmtMtd(PaymentMethod3Code.TRF);
        paymentInstructionInformationSCT3.setBtchBookg(false);
        paymentInstructionInformationSCT3.setNbOfTxs(String.valueOf(this.creditTransfer.getTransactions().size()));
        paymentInstructionInformationSCT3.setCtrlSum(this.creditTransfer.getTotalAmount());
        paymentInstructionInformationSCT3.setDbtr(partyIdentification(this.creditTransfer.getDebtor().orElse(null)));
        paymentInstructionInformationSCT3.setDbtrAcct(cashAccount(this.creditTransfer.getDebtorAccount()));
        branchAndFinancialInstitutionIdentification(this.creditTransfer.getDebtorAccount()).ifPresent(paymentInstructionInformationSCT3::setDbtrAgt);

        this.creditTransfer.getServiceLevelCode().ifPresent(serviceLevelCode -> {
            ServiceLevel8Choice serviceLevel = new ServiceLevel8Choice();
            serviceLevel.setCd(serviceLevelCode);
            PaymentTypeInformation19 paymentTypeInformation = new PaymentTypeInformation19();
            paymentTypeInformation.setSvcLvl(serviceLevel);
            paymentInstructionInformationSCT3.setPmtTpInf(paymentTypeInformation);
        });

        paymentInstructionInformationSCT3.setReqdExctnDt(this.datatypeFactory.newXMLGregorianCalendar(DateTimeFormatter.ISO_LOCAL_DATE.format(this.creditTransfer.getRequestedExecutionDate())));

        paymentInstructionInformationSCT3.setChrgBr(ChargeBearerType1Code.SLEV);

        for (Transaction transaction : this.creditTransfer.getTransactions()) {
            paymentInstructionInformationSCT3.getCdtTrfTxInf().add(transaction(transaction));
        }

        cti.getPmtInf().add(paymentInstructionInformationSCT3);

        return cti;
    }

    private GroupHeader32 header() {
        GroupHeader32 head = new GroupHeader32();
        head.setMsgId(this.creditTransfer.getId());
        head.setCreDtTm(this.datatypeFactory.newXMLGregorianCalendar(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(this.creditTransfer.getCreationDateTime())));
        head.setNbOfTxs(String.valueOf(this.creditTransfer.getTransactions().size()));
        head.setCtrlSum(this.creditTransfer.getTotalAmount());
        head.setInitgPty(partyIdentification(this.creditTransfer.getDebtor().orElse(null)));
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
        branchAndFinancialInstitutionIdentification(transaction.getAccount()).ifPresent(creditTransferTransactionInformation::setCdtrAgt);

        return creditTransferTransactionInformation;
    }

    private Optional<BranchAndFinancialInstitutionIdentification4> branchAndFinancialInstitutionIdentification(BankAccount bankAccount) {
        return bankAccount.getBic().map(bic -> {
            BicUtil.validate(bic);
            FinancialInstitutionIdentification7 financialInstitutionIdentification = new FinancialInstitutionIdentification7();
            financialInstitutionIdentification.setBIC(bic);
            BranchAndFinancialInstitutionIdentification4 branchAndFinancialInstitutionIdentification = new BranchAndFinancialInstitutionIdentification4();
            branchAndFinancialInstitutionIdentification.setFinInstnId(financialInstitutionIdentification);
            return branchAndFinancialInstitutionIdentification;
        });
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
            throw new Bank4jException("IBAN or otherId must be provided");
        }
        return accountIdentification;
    }

}
