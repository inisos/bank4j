package io.inisos.bank4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.inisos.bank4j.CustomerCreditTransferInitiationVersion.V03_CH_02;

/**
 * @author Valentin Hutter
 */
class CreditTransferV03Ch02Test {

    @Test
    void test_with_all_fields() {

        final Party debtor = Bank.simpleParty()
                .name("Banque Cantonale de Geneve")
                .postalAddress(Bank.simplePostalAddress()
                        .type("ADDR")
                        .streetName("Rue du Rhone")
                        .buildingNumber("8")
                        .postCode("1204")
                        .townName("Geneve")
                        .countrySubDivision("GE")
                        .country("CH")
                        .build())
                .build();
        final BankAccount debtorAccount = Bank.simpleBankAccount()
                .iban("CH9300762011623852957")
                .bic("UBSWCHZH80A")
                .build();

        List<Transaction> transactions = new ArrayList<>();
        for (int i = 1; i < 3; i++) {
            transactions.add(Bank.simpleTransaction()
                    .party(Bank.simpleParty()
                            .name("Beneficiary " + i)
                            .postalAddress(Bank.simplePostalAddress()
                                    .type("ADDR")
                                    .streetName("Rue du Rhone")
                                    .buildingNumber("8")
                                    .postCode("1204")
                                    .townName("Geneve")
                                    .countrySubDivision("GE")
                                    .country("CH")
                                    .build())
                            .build())
                    .account(Bank.simpleBankAccount()
                            .iban("CH5604835012345678009")
                            .bic("RAIFCH22XXX")
                            .build())
                    .amount(i + "12.34")
                    .currency("EUR")
                    .endToEndId("ENDTOEND" + i)
                    .id("ID" + i)
                    .chargeBearerCode(ChargeBearer.CRED)
                    .remittanceInformationUnstructured(Collections.singleton("My unstructured remittance information"))
                    .build());
        }

        LocalDateTime newYear2021 = LocalDateTime.of(2021, 1, 1, 0, 0, 0, 0);
        LocalDate nextDay = newYear2021.plusDays(1).toLocalDate();

        CreditTransferOperation creditTransfer = Bank.jaxbCreditTransferSepa(V03_CH_02)
                .instructionPriority(Priority.HIGH)
                .debtor(debtor)
                .debtorAccount(debtorAccount)
                .transactions(transactions)
                .id("MYID")
                .creationDateTime(newYear2021)
                .requestedExecutionDate(nextDay)
                .chargeBearer(ChargeBearer.DEBT)
                .build();

        String xml = creditTransfer.marshal();

        Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Document xsi:schemaLocation=\"http://www.six-interbank-clearing.com/de/pain.001.001.03.ch.02.xsd\" xmlns=\"http://www.six-interbank-clearing.com/de/pain.001.001.03.ch.02.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><CstmrCdtTrfInitn><GrpHdr><MsgId>MYID</MsgId><CreDtTm>2021-01-01T00:00:00</CreDtTm><NbOfTxs>2</NbOfTxs><CtrlSum>324.68</CtrlSum><InitgPty><Nm>Banque Cantonale de Geneve</Nm></InitgPty></GrpHdr><PmtInf><PmtInfId>MYID</PmtInfId><PmtMtd>TRF</PmtMtd><BtchBookg>false</BtchBookg><NbOfTxs>2</NbOfTxs><CtrlSum>324.68</CtrlSum><PmtTpInf><InstrPrty>HIGH</InstrPrty><SvcLvl><Cd>SEPA</Cd></SvcLvl></PmtTpInf><ReqdExctnDt>2021-01-02</ReqdExctnDt><Dbtr><Nm>Banque Cantonale de Geneve</Nm><PstlAdr><AdrTp>ADDR</AdrTp><StrtNm>Rue du Rhone</StrtNm><BldgNb>8</BldgNb><PstCd>1204</PstCd><TwnNm>Geneve</TwnNm><CtrySubDvsn>GE</CtrySubDvsn><Ctry>CH</Ctry></PstlAdr></Dbtr><DbtrAcct><Id><IBAN>CH9300762011623852957</IBAN></Id></DbtrAcct><DbtrAgt><FinInstnId><BIC>UBSWCHZH80A</BIC></FinInstnId></DbtrAgt><ChrgBr>DEBT</ChrgBr><CdtTrfTxInf><PmtId><InstrId>ID1</InstrId><EndToEndId>ENDTOEND1</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">112.34</InstdAmt></Amt><ChrgBr>CRED</ChrgBr><CdtrAgt><FinInstnId><BIC>RAIFCH22XXX</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 1</Nm><PstlAdr><AdrTp>ADDR</AdrTp><StrtNm>Rue du Rhone</StrtNm><BldgNb>8</BldgNb><PstCd>1204</PstCd><TwnNm>Geneve</TwnNm><CtrySubDvsn>GE</CtrySubDvsn><Ctry>CH</Ctry></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>CH5604835012345678009</IBAN></Id></CdtrAcct><RmtInf><Ustrd>My unstructured remittance information</Ustrd></RmtInf></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID2</InstrId><EndToEndId>ENDTOEND2</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">212.34</InstdAmt></Amt><ChrgBr>CRED</ChrgBr><CdtrAgt><FinInstnId><BIC>RAIFCH22XXX</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 2</Nm><PstlAdr><AdrTp>ADDR</AdrTp><StrtNm>Rue du Rhone</StrtNm><BldgNb>8</BldgNb><PstCd>1204</PstCd><TwnNm>Geneve</TwnNm><CtrySubDvsn>GE</CtrySubDvsn><Ctry>CH</Ctry></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>CH5604835012345678009</IBAN></Id></CdtrAcct><RmtInf><Ustrd>My unstructured remittance information</Ustrd></RmtInf></CdtTrfTxInf></PmtInf></CstmrCdtTrfInitn></Document>",
                xml);

        assertValid(xml);
    }

    @Test
    void test_with_all_fields_and_batch_booking() {

        final Party debtor = Bank.simpleParty()
                .name("Banque Cantonale de Geneve")
                .build();
        final BankAccount debtorAccount = Bank.simpleBankAccount()
                .iban("CH9300762011623852957")
                .bic("UBSWCHZH80A")
                .build();

        List<Transaction> transactions = new ArrayList<>();
        for (int i = 1; i < 3; i++) {
            transactions.add(Bank.simpleTransaction()
                    .party(Bank.simpleParty()
                            .name("Beneficiary " + i)
                            .build())
                    .account(Bank.simpleBankAccount()
                            .iban("CH5604835012345678009")
                            .bic("RAIFCH22XXX")
                            .build())
                    .amount(i + "12.34")
                    .currency("EUR")
                    .endToEndId("ENDTOEND" + i)
                    .id("ID" + i)
                    .remittanceInformationUnstructured(Collections.singleton("My unstructured remittance information"))
                    .build());
        }

        LocalDateTime newYear2021 = LocalDateTime.of(2021, 1, 1, 0, 0, 0, 0);
        LocalDate nextDay = newYear2021.plusDays(1).toLocalDate();

        CreditTransferOperation creditTransfer = Bank.jaxbCreditTransferSepa(V03_CH_02)
                .debtor(debtor)
                .debtorAccount(debtorAccount)
                .transactions(transactions)
                .id("MYID")
                .creationDateTime(newYear2021)
                .requestedExecutionDate(nextDay)
                .batchBooking(true)
                .build();

        String xml = creditTransfer.marshal();

        Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Document xsi:schemaLocation=\"http://www.six-interbank-clearing.com/de/pain.001.001.03.ch.02.xsd\" xmlns=\"http://www.six-interbank-clearing.com/de/pain.001.001.03.ch.02.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><CstmrCdtTrfInitn><GrpHdr><MsgId>MYID</MsgId><CreDtTm>2021-01-01T00:00:00</CreDtTm><NbOfTxs>2</NbOfTxs><CtrlSum>324.68</CtrlSum><InitgPty><Nm>Banque Cantonale de Geneve</Nm></InitgPty></GrpHdr><PmtInf><PmtInfId>MYID</PmtInfId><PmtMtd>TRF</PmtMtd><BtchBookg>true</BtchBookg><NbOfTxs>2</NbOfTxs><CtrlSum>324.68</CtrlSum><PmtTpInf><SvcLvl><Cd>SEPA</Cd></SvcLvl></PmtTpInf><ReqdExctnDt>2021-01-02</ReqdExctnDt><Dbtr><Nm>Banque Cantonale de Geneve</Nm></Dbtr><DbtrAcct><Id><IBAN>CH9300762011623852957</IBAN></Id></DbtrAcct><DbtrAgt><FinInstnId><BIC>UBSWCHZH80A</BIC></FinInstnId></DbtrAgt><ChrgBr>SLEV</ChrgBr><CdtTrfTxInf><PmtId><InstrId>ID1</InstrId><EndToEndId>ENDTOEND1</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">112.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>RAIFCH22XXX</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 1</Nm></Cdtr><CdtrAcct><Id><IBAN>CH5604835012345678009</IBAN></Id></CdtrAcct><RmtInf><Ustrd>My unstructured remittance information</Ustrd></RmtInf></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID2</InstrId><EndToEndId>ENDTOEND2</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">212.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>RAIFCH22XXX</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 2</Nm></Cdtr><CdtrAcct><Id><IBAN>CH5604835012345678009</IBAN></Id></CdtrAcct><RmtInf><Ustrd>My unstructured remittance information</Ustrd></RmtInf></CdtTrfTxInf></PmtInf></CstmrCdtTrfInitn></Document>",
                xml);

        assertValid(xml);
    }

    @Test
    void test_without_optional_fields() {

        final Party debtor = Bank.simpleParty()
                .name("Banque Cantonale de Geneve")
                .build();
        final BankAccount debtorAccount = Bank.simpleBankAccount()
                .iban("CH9300762011623852957")
                .build();

        List<Transaction> transactions = new ArrayList<>();
        for (int i = 1; i < 3; i++) {
            transactions.add(Bank.simpleTransaction()
                    .party(Bank.simpleParty()
                            .name("Beneficiary " + i)
                            .build())
                    .account(Bank.simpleBankAccount()
                            .iban("CH5604835012345678009")
                            .build())
                    .amount(i + "12.34")
                    .currency("EUR")
                    .endToEndId("ENDTOEND" + i)
                    .build());
        }

        LocalDateTime newYear2021 = LocalDateTime.of(2021, 1, 1, 0, 0, 0, 0);
        LocalDate nextDay = newYear2021.plusDays(1).toLocalDate();

        CreditTransferOperation creditTransfer = Bank.jaxbCreditTransferSepa(V03_CH_02)
                .debtor(debtor)
                .debtorAccount(debtorAccount)
                .transactions(transactions)
                .creationDateTime(newYear2021)
                .requestedExecutionDate(nextDay)
                .build();

        String xml = creditTransfer.marshal();

        Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Document xsi:schemaLocation=\"http://www.six-interbank-clearing.com/de/pain.001.001.03.ch.02.xsd\" xmlns=\"http://www.six-interbank-clearing.com/de/pain.001.001.03.ch.02.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><CstmrCdtTrfInitn><GrpHdr><MsgId>20210101120000</MsgId><CreDtTm>2021-01-01T00:00:00</CreDtTm><NbOfTxs>2</NbOfTxs><CtrlSum>324.68</CtrlSum><InitgPty><Nm>Banque Cantonale de Geneve</Nm></InitgPty></GrpHdr><PmtInf><PmtInfId>20210101120000</PmtInfId><PmtMtd>TRF</PmtMtd><BtchBookg>false</BtchBookg><NbOfTxs>2</NbOfTxs><CtrlSum>324.68</CtrlSum><PmtTpInf><SvcLvl><Cd>SEPA</Cd></SvcLvl></PmtTpInf><ReqdExctnDt>2021-01-02</ReqdExctnDt><Dbtr><Nm>Banque Cantonale de Geneve</Nm></Dbtr><DbtrAcct><Id><IBAN>CH9300762011623852957</IBAN></Id></DbtrAcct><DbtrAgt><FinInstnId/></DbtrAgt><ChrgBr>SLEV</ChrgBr><CdtTrfTxInf><PmtId><EndToEndId>ENDTOEND1</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">112.34</InstdAmt></Amt><Cdtr><Nm>Beneficiary 1</Nm></Cdtr><CdtrAcct><Id><IBAN>CH5604835012345678009</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><EndToEndId>ENDTOEND2</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">212.34</InstdAmt></Amt><Cdtr><Nm>Beneficiary 2</Nm></Cdtr><CdtrAcct><Id><IBAN>CH5604835012345678009</IBAN></Id></CdtrAcct></CdtTrfTxInf></PmtInf></CstmrCdtTrfInitn></Document>",
                xml);

        assertValid(xml);
    }

    private void assertValid(String xml) {
        Assertions.assertDoesNotThrow(() -> SchemaValidator.validateCreditTransfer(V03_CH_02, new StringReader(xml)));
    }
}
