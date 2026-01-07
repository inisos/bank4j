package io.inisos.bank4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.inisos.bank4j.CustomerCreditTransferInitiationVersion.V003_03;

/**
 * @author Valentin Hutter
 */
class CreditTransfer003V03Test {

    @Test
    void test_with_all_fields() {

        final Party debtor = Bank.simpleParty()
                .name("Banque de France")
                .postalAddress(Bank.simplePostalAddress()
                        .addressLine("115 rue de Sevres")
                        .addressLine("75006 Paris")
                        .country("FR")
                        .build())
                .build();
        final BankAccount debtorAccount = Bank.simpleBankAccount()
                .iban("FR7630001007941234567890185")
                .bic("BDFEFRPPXXX")
                .build();

        List<Transaction> transactions = new ArrayList<>();
        for (int i = 1; i < 3; i++) {
            transactions.add(Bank.simpleTransaction()
                    .party(Bank.simpleParty()
                            .name("Beneficiary " + i)
                            .postalAddress(Bank.simplePostalAddress()
                                    .addressLine("115 rue de Sevres")
                                    .addressLine("75006 Paris")
                                    .country("FR")
                                    .build())
                            .build())
                    .account(Bank.simpleBankAccount()
                            .iban("FR7610011000201234567890188")
                            .bic("PSSTFRPP")
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

        CreditTransferOperation creditTransfer = Bank.jaxbCreditTransferSepa(V003_03)
                .instructionPriority(Priority.HIGH)
                .debtor(debtor)
                .debtorAccount(debtorAccount)
                .transactions(transactions)
                .id("MYID")
                .creationDateTime(newYear2021)
                .requestedExecutionDate(nextDay)
                .chargeBearer(ChargeBearer.SLEV)
                .build();

        String xml = creditTransfer.marshal();

        Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Document xsi:schemaLocation=\"urn:iso:std:iso:20022:tech:xsd:pain.001.003.03.xsd\" xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.003.03\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><CstmrCdtTrfInitn><GrpHdr><MsgId>MYID</MsgId><CreDtTm>2021-01-01T00:00:00</CreDtTm><NbOfTxs>2</NbOfTxs><CtrlSum>324.68</CtrlSum><InitgPty><Nm>Banque de France</Nm></InitgPty></GrpHdr><PmtInf><PmtInfId>MYID</PmtInfId><PmtMtd>TRF</PmtMtd><BtchBookg>false</BtchBookg><NbOfTxs>2</NbOfTxs><CtrlSum>324.68</CtrlSum><PmtTpInf><InstrPrty>HIGH</InstrPrty><SvcLvl><Cd>SEPA</Cd></SvcLvl></PmtTpInf><ReqdExctnDt>2021-01-02</ReqdExctnDt><Dbtr><Nm>Banque de France</Nm><PstlAdr><Ctry>FR</Ctry><AdrLine>115 rue de Sevres</AdrLine><AdrLine>75006 Paris</AdrLine></PstlAdr></Dbtr><DbtrAcct><Id><IBAN>FR7630001007941234567890185</IBAN></Id></DbtrAcct><DbtrAgt><FinInstnId><BIC>BDFEFRPPXXX</BIC></FinInstnId></DbtrAgt><ChrgBr>SLEV</ChrgBr><CdtTrfTxInf><PmtId><InstrId>ID1</InstrId><EndToEndId>ENDTOEND1</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">112.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 1</Nm><PstlAdr><Ctry>FR</Ctry><AdrLine>115 rue de Sevres</AdrLine><AdrLine>75006 Paris</AdrLine></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct><RmtInf><Ustrd>My unstructured remittance information</Ustrd></RmtInf></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID2</InstrId><EndToEndId>ENDTOEND2</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">212.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 2</Nm><PstlAdr><Ctry>FR</Ctry><AdrLine>115 rue de Sevres</AdrLine><AdrLine>75006 Paris</AdrLine></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct><RmtInf><Ustrd>My unstructured remittance information</Ustrd></RmtInf></CdtTrfTxInf></PmtInf></CstmrCdtTrfInitn></Document>",
                xml);

        assertValid(xml);
    }

    @Test
    void test_with_all_fields_and_batch_booking() {

        final Party debtor = Bank.simpleParty()
                .name("Banque de France")
                .postalAddress(Bank.simplePostalAddress()
                        .addressLine("115 rue de Sevres")
                        .addressLine("75006 Paris")
                        .country("FR")
                        .build())
                .build();
        final BankAccount debtorAccount = Bank.simpleBankAccount()
                .iban("FR7630001007941234567890185")
                .bic("BDFEFRPPXXX")
                .build();

        List<Transaction> transactions = new ArrayList<>();
        for (int i = 1; i < 3; i++) {
            transactions.add(Bank.simpleTransaction()
                    .party(Bank.simpleParty()
                            .name("Beneficiary " + i)
                            .postalAddress(Bank.simplePostalAddress()
                                    .addressLine("115 rue de Sevres")
                                    .addressLine("75006 Paris")
                                    .country("FR")
                                    .build())
                            .build())
                    .account(Bank.simpleBankAccount()
                            .iban("FR7610011000201234567890188")
                            .bic("PSSTFRPP")
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

        CreditTransferOperation creditTransfer = Bank.jaxbCreditTransferSepa(V003_03)
                .debtor(debtor)
                .debtorAccount(debtorAccount)
                .transactions(transactions)
                .id("MYID")
                .creationDateTime(newYear2021)
                .requestedExecutionDate(nextDay)
                .batchBooking(true)
                .build();

        String xml = creditTransfer.marshal();

        Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Document xsi:schemaLocation=\"urn:iso:std:iso:20022:tech:xsd:pain.001.003.03.xsd\" xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.003.03\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><CstmrCdtTrfInitn><GrpHdr><MsgId>MYID</MsgId><CreDtTm>2021-01-01T00:00:00</CreDtTm><NbOfTxs>2</NbOfTxs><CtrlSum>324.68</CtrlSum><InitgPty><Nm>Banque de France</Nm></InitgPty></GrpHdr><PmtInf><PmtInfId>MYID</PmtInfId><PmtMtd>TRF</PmtMtd><BtchBookg>true</BtchBookg><NbOfTxs>2</NbOfTxs><CtrlSum>324.68</CtrlSum><PmtTpInf><SvcLvl><Cd>SEPA</Cd></SvcLvl></PmtTpInf><ReqdExctnDt>2021-01-02</ReqdExctnDt><Dbtr><Nm>Banque de France</Nm><PstlAdr><Ctry>FR</Ctry><AdrLine>115 rue de Sevres</AdrLine><AdrLine>75006 Paris</AdrLine></PstlAdr></Dbtr><DbtrAcct><Id><IBAN>FR7630001007941234567890185</IBAN></Id></DbtrAcct><DbtrAgt><FinInstnId><BIC>BDFEFRPPXXX</BIC></FinInstnId></DbtrAgt><ChrgBr>SLEV</ChrgBr><CdtTrfTxInf><PmtId><InstrId>ID1</InstrId><EndToEndId>ENDTOEND1</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">112.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 1</Nm><PstlAdr><Ctry>FR</Ctry><AdrLine>115 rue de Sevres</AdrLine><AdrLine>75006 Paris</AdrLine></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct><RmtInf><Ustrd>My unstructured remittance information</Ustrd></RmtInf></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID2</InstrId><EndToEndId>ENDTOEND2</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">212.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 2</Nm><PstlAdr><Ctry>FR</Ctry><AdrLine>115 rue de Sevres</AdrLine><AdrLine>75006 Paris</AdrLine></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct><RmtInf><Ustrd>My unstructured remittance information</Ustrd></RmtInf></CdtTrfTxInf></PmtInf></CstmrCdtTrfInitn></Document>",
                xml);

        assertValid(xml);
    }

    @Test
    void test_without_optional_fields() {

        final Party debtor = Bank.simpleParty()
                .name("Banque de France")
                .build();
        final BankAccount debtorAccount = Bank.simpleBankAccount()
                .iban("FR7630001007941234567890185")
                .build();

        List<Transaction> transactions = new ArrayList<>();
        for (int i = 1; i < 3; i++) {
            transactions.add(Bank.simpleTransaction()
                    .party(Bank.simpleParty()
                            .name("Beneficiary " + i)
                            .build())
                    .account(Bank.simpleBankAccount()
                            .iban("FR7610011000201234567890188")
                            .build())
                    .amount(i + "12.34")
                    .currency("EUR")
                    .endToEndId("ENDTOEND" + i)
                    .build());
        }

        LocalDateTime newYear2021 = LocalDateTime.of(2021, 1, 1, 0, 0, 0, 0);
        LocalDate nextDay = newYear2021.plusDays(1).toLocalDate();

        CreditTransferOperation creditTransfer = Bank.jaxbCreditTransferSepa(V003_03)
                .debtor(debtor)
                .debtorAccount(debtorAccount)
                .transactions(transactions)
                .creationDateTime(newYear2021)
                .requestedExecutionDate(nextDay)
                .build();

        String xml = creditTransfer.marshal();

        Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Document xsi:schemaLocation=\"urn:iso:std:iso:20022:tech:xsd:pain.001.003.03.xsd\" xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.003.03\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><CstmrCdtTrfInitn><GrpHdr><MsgId>20210101120000</MsgId><CreDtTm>2021-01-01T00:00:00</CreDtTm><NbOfTxs>2</NbOfTxs><CtrlSum>324.68</CtrlSum><InitgPty><Nm>Banque de France</Nm></InitgPty></GrpHdr><PmtInf><PmtInfId>20210101120000</PmtInfId><PmtMtd>TRF</PmtMtd><BtchBookg>false</BtchBookg><NbOfTxs>2</NbOfTxs><CtrlSum>324.68</CtrlSum><PmtTpInf><SvcLvl><Cd>SEPA</Cd></SvcLvl></PmtTpInf><ReqdExctnDt>2021-01-02</ReqdExctnDt><Dbtr><Nm>Banque de France</Nm></Dbtr><DbtrAcct><Id><IBAN>FR7630001007941234567890185</IBAN></Id></DbtrAcct><DbtrAgt><FinInstnId><Othr><Id>NOTPROVIDED</Id></Othr></FinInstnId></DbtrAgt><ChrgBr>SLEV</ChrgBr><CdtTrfTxInf><PmtId><EndToEndId>ENDTOEND1</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">112.34</InstdAmt></Amt><Cdtr><Nm>Beneficiary 1</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><EndToEndId>ENDTOEND2</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">212.34</InstdAmt></Amt><Cdtr><Nm>Beneficiary 2</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf></PmtInf></CstmrCdtTrfInitn></Document>",
                xml);

        assertValid(xml);
    }

    private void assertValid(String xml) {
        Assertions.assertDoesNotThrow(() -> SchemaValidator.validateCreditTransfer(V003_03, new StringReader(xml)));
    }
}
