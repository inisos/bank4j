package io.inisos.bank4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.inisos.bank4j.CustomerCreditTransferInitiationVersion.V03;

/**
 * @author Patrice Blanchardie
 */
class CreditTransferV03Test {

    @Test
    void test_with_all_fields() {

        final Party debtor = Bank.simpleParty()
                .name("Banque de France")
                .build();
        final BankAccount debtorAccount = Bank.simpleBankAccount()
                .iban("FR7630001007941234567890185")
                .bic("BDFEFRPPXXX")
                .name("Compte de la Banque de France")
                .build();

        List<Transaction> transactions = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            transactions.add(Bank.simpleTransaction()
                    .party(Bank.simpleParty()
                            .name("Debtor " + i)
                            .postalAddress(Bank.simplePostalAddress()
                                    .type("ADDR")
                                    .department("Dept")
                                    .subDepartment("SubDept")
                                    .streetName("115 rue de Sèvres")
                                    .postCode("75006")
                                    .townName("Paris")
                                    .countrySubDivision("IDF")
                                    .country("FR")
                                    .build())
                            .build())
                    .account(Bank.simpleBankAccount()
                            .iban("FR7610011000201234567890188")
                            .bic("PSSTFRPP")
                            .name("Debtor Account " + i)
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

        CreditTransferOperation creditTransfer = Bank.jaxbCreditTransferSepa()
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

        Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.001.03\"><CstmrCdtTrfInitn><GrpHdr><MsgId>MYID</MsgId><CreDtTm>2021-01-01T00:00:00</CreDtTm><NbOfTxs>9</NbOfTxs><CtrlSum>4611.06</CtrlSum><InitgPty><Nm>Banque de France</Nm></InitgPty></GrpHdr><PmtInf><PmtInfId>MYID</PmtInfId><PmtMtd>TRF</PmtMtd><BtchBookg>false</BtchBookg><NbOfTxs>9</NbOfTxs><CtrlSum>4611.06</CtrlSum><PmtTpInf><InstrPrty>HIGH</InstrPrty><SvcLvl><Cd>SEPA</Cd></SvcLvl></PmtTpInf><ReqdExctnDt>2021-01-02</ReqdExctnDt><Dbtr><Nm>Banque de France</Nm></Dbtr><DbtrAcct><Id><IBAN>FR7630001007941234567890185</IBAN></Id><Nm>Compte de la Banque de France</Nm></DbtrAcct><DbtrAgt><FinInstnId><BIC>BDFEFRPPXXX</BIC></FinInstnId></DbtrAgt><ChrgBr>DEBT</ChrgBr><CdtTrfTxInf><PmtId><InstrId>ID1</InstrId><EndToEndId>ENDTOEND1</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">112.34</InstdAmt></Amt><ChrgBr>CRED</ChrgBr><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Debtor 1</Nm><PstlAdr><AdrTp>ADDR</AdrTp><Dept>Dept</Dept><SubDept>SubDept</SubDept><StrtNm>115 rue de Sèvres</StrtNm><PstCd>75006</PstCd><TwnNm>Paris</TwnNm><CtrySubDvsn>IDF</CtrySubDvsn><Ctry>FR</Ctry></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id><Nm>Debtor Account 1</Nm></CdtrAcct><RmtInf><Ustrd>My unstructured remittance information</Ustrd></RmtInf></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID2</InstrId><EndToEndId>ENDTOEND2</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">212.34</InstdAmt></Amt><ChrgBr>CRED</ChrgBr><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Debtor 2</Nm><PstlAdr><AdrTp>ADDR</AdrTp><Dept>Dept</Dept><SubDept>SubDept</SubDept><StrtNm>115 rue de Sèvres</StrtNm><PstCd>75006</PstCd><TwnNm>Paris</TwnNm><CtrySubDvsn>IDF</CtrySubDvsn><Ctry>FR</Ctry></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id><Nm>Debtor Account 2</Nm></CdtrAcct><RmtInf><Ustrd>My unstructured remittance information</Ustrd></RmtInf></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID3</InstrId><EndToEndId>ENDTOEND3</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">312.34</InstdAmt></Amt><ChrgBr>CRED</ChrgBr><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Debtor 3</Nm><PstlAdr><AdrTp>ADDR</AdrTp><Dept>Dept</Dept><SubDept>SubDept</SubDept><StrtNm>115 rue de Sèvres</StrtNm><PstCd>75006</PstCd><TwnNm>Paris</TwnNm><CtrySubDvsn>IDF</CtrySubDvsn><Ctry>FR</Ctry></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id><Nm>Debtor Account 3</Nm></CdtrAcct><RmtInf><Ustrd>My unstructured remittance information</Ustrd></RmtInf></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID4</InstrId><EndToEndId>ENDTOEND4</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">412.34</InstdAmt></Amt><ChrgBr>CRED</ChrgBr><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Debtor 4</Nm><PstlAdr><AdrTp>ADDR</AdrTp><Dept>Dept</Dept><SubDept>SubDept</SubDept><StrtNm>115 rue de Sèvres</StrtNm><PstCd>75006</PstCd><TwnNm>Paris</TwnNm><CtrySubDvsn>IDF</CtrySubDvsn><Ctry>FR</Ctry></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id><Nm>Debtor Account 4</Nm></CdtrAcct><RmtInf><Ustrd>My unstructured remittance information</Ustrd></RmtInf></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID5</InstrId><EndToEndId>ENDTOEND5</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">512.34</InstdAmt></Amt><ChrgBr>CRED</ChrgBr><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Debtor 5</Nm><PstlAdr><AdrTp>ADDR</AdrTp><Dept>Dept</Dept><SubDept>SubDept</SubDept><StrtNm>115 rue de Sèvres</StrtNm><PstCd>75006</PstCd><TwnNm>Paris</TwnNm><CtrySubDvsn>IDF</CtrySubDvsn><Ctry>FR</Ctry></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id><Nm>Debtor Account 5</Nm></CdtrAcct><RmtInf><Ustrd>My unstructured remittance information</Ustrd></RmtInf></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID6</InstrId><EndToEndId>ENDTOEND6</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">612.34</InstdAmt></Amt><ChrgBr>CRED</ChrgBr><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Debtor 6</Nm><PstlAdr><AdrTp>ADDR</AdrTp><Dept>Dept</Dept><SubDept>SubDept</SubDept><StrtNm>115 rue de Sèvres</StrtNm><PstCd>75006</PstCd><TwnNm>Paris</TwnNm><CtrySubDvsn>IDF</CtrySubDvsn><Ctry>FR</Ctry></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id><Nm>Debtor Account 6</Nm></CdtrAcct><RmtInf><Ustrd>My unstructured remittance information</Ustrd></RmtInf></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID7</InstrId><EndToEndId>ENDTOEND7</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">712.34</InstdAmt></Amt><ChrgBr>CRED</ChrgBr><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Debtor 7</Nm><PstlAdr><AdrTp>ADDR</AdrTp><Dept>Dept</Dept><SubDept>SubDept</SubDept><StrtNm>115 rue de Sèvres</StrtNm><PstCd>75006</PstCd><TwnNm>Paris</TwnNm><CtrySubDvsn>IDF</CtrySubDvsn><Ctry>FR</Ctry></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id><Nm>Debtor Account 7</Nm></CdtrAcct><RmtInf><Ustrd>My unstructured remittance information</Ustrd></RmtInf></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID8</InstrId><EndToEndId>ENDTOEND8</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">812.34</InstdAmt></Amt><ChrgBr>CRED</ChrgBr><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Debtor 8</Nm><PstlAdr><AdrTp>ADDR</AdrTp><Dept>Dept</Dept><SubDept>SubDept</SubDept><StrtNm>115 rue de Sèvres</StrtNm><PstCd>75006</PstCd><TwnNm>Paris</TwnNm><CtrySubDvsn>IDF</CtrySubDvsn><Ctry>FR</Ctry></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id><Nm>Debtor Account 8</Nm></CdtrAcct><RmtInf><Ustrd>My unstructured remittance information</Ustrd></RmtInf></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID9</InstrId><EndToEndId>ENDTOEND9</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">912.34</InstdAmt></Amt><ChrgBr>CRED</ChrgBr><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Debtor 9</Nm><PstlAdr><AdrTp>ADDR</AdrTp><Dept>Dept</Dept><SubDept>SubDept</SubDept><StrtNm>115 rue de Sèvres</StrtNm><PstCd>75006</PstCd><TwnNm>Paris</TwnNm><CtrySubDvsn>IDF</CtrySubDvsn><Ctry>FR</Ctry></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id><Nm>Debtor Account 9</Nm></CdtrAcct><RmtInf><Ustrd>My unstructured remittance information</Ustrd></RmtInf></CdtTrfTxInf></PmtInf></CstmrCdtTrfInitn></Document>",
                xml);

        assertValid(xml);
    }

    @Test
    void test_with_all_fields_and_batchBooking() {

        final Party debtor = Bank.simpleParty()
                .name("Banque de France")
                .build();
        final BankAccount debtorAccount = Bank.simpleBankAccount()
                .iban("FR7630001007941234567890185")
                .bic("BDFEFRPPXXX")
                .name("Compte de la Banque de France")
                .build();

        List<Transaction> transactions = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            transactions.add(Bank.simpleTransaction()
                    .party(Bank.simpleParty()
                            .name("Debtor " + i)
                            .postalAddress(Bank.simplePostalAddress()
                                    .type("ADDR")
                                    .department("Dept")
                                    .subDepartment("SubDept")
                                    .streetName("115 rue de Sèvres")
                                    .postCode("75006")
                                    .townName("Paris")
                                    .countrySubDivision("IDF")
                                    .country("FR")
                                    .build())
                            .build())
                    .account(Bank.simpleBankAccount()
                            .iban("FR7610011000201234567890188")
                            .bic("PSSTFRPP")
                            .name("Debtor Account " + i)
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

        CreditTransferOperation creditTransfer = Bank.jaxbCreditTransferSepa()
                .debtor(debtor)
                .debtorAccount(debtorAccount)
                .transactions(transactions)
                .id("MYID")
                .creationDateTime(newYear2021)
                .requestedExecutionDate(nextDay)
                .chargeBearer(ChargeBearer.DEBT)
                .batchBooking(true)
                .build();

        String xml = creditTransfer.marshal();

        Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.001.03\"><CstmrCdtTrfInitn><GrpHdr><MsgId>MYID</MsgId><CreDtTm>2021-01-01T00:00:00</CreDtTm><NbOfTxs>9</NbOfTxs><CtrlSum>4611.06</CtrlSum><InitgPty><Nm>Banque de France</Nm></InitgPty></GrpHdr><PmtInf><PmtInfId>MYID</PmtInfId><PmtMtd>TRF</PmtMtd><BtchBookg>true</BtchBookg><NbOfTxs>9</NbOfTxs><CtrlSum>4611.06</CtrlSum><PmtTpInf><SvcLvl><Cd>SEPA</Cd></SvcLvl></PmtTpInf><ReqdExctnDt>2021-01-02</ReqdExctnDt><Dbtr><Nm>Banque de France</Nm></Dbtr><DbtrAcct><Id><IBAN>FR7630001007941234567890185</IBAN></Id><Nm>Compte de la Banque de France</Nm></DbtrAcct><DbtrAgt><FinInstnId><BIC>BDFEFRPPXXX</BIC></FinInstnId></DbtrAgt><ChrgBr>DEBT</ChrgBr><CdtTrfTxInf><PmtId><InstrId>ID1</InstrId><EndToEndId>ENDTOEND1</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">112.34</InstdAmt></Amt><ChrgBr>CRED</ChrgBr><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Debtor 1</Nm><PstlAdr><AdrTp>ADDR</AdrTp><Dept>Dept</Dept><SubDept>SubDept</SubDept><StrtNm>115 rue de Sèvres</StrtNm><PstCd>75006</PstCd><TwnNm>Paris</TwnNm><CtrySubDvsn>IDF</CtrySubDvsn><Ctry>FR</Ctry></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id><Nm>Debtor Account 1</Nm></CdtrAcct><RmtInf><Ustrd>My unstructured remittance information</Ustrd></RmtInf></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID2</InstrId><EndToEndId>ENDTOEND2</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">212.34</InstdAmt></Amt><ChrgBr>CRED</ChrgBr><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Debtor 2</Nm><PstlAdr><AdrTp>ADDR</AdrTp><Dept>Dept</Dept><SubDept>SubDept</SubDept><StrtNm>115 rue de Sèvres</StrtNm><PstCd>75006</PstCd><TwnNm>Paris</TwnNm><CtrySubDvsn>IDF</CtrySubDvsn><Ctry>FR</Ctry></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id><Nm>Debtor Account 2</Nm></CdtrAcct><RmtInf><Ustrd>My unstructured remittance information</Ustrd></RmtInf></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID3</InstrId><EndToEndId>ENDTOEND3</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">312.34</InstdAmt></Amt><ChrgBr>CRED</ChrgBr><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Debtor 3</Nm><PstlAdr><AdrTp>ADDR</AdrTp><Dept>Dept</Dept><SubDept>SubDept</SubDept><StrtNm>115 rue de Sèvres</StrtNm><PstCd>75006</PstCd><TwnNm>Paris</TwnNm><CtrySubDvsn>IDF</CtrySubDvsn><Ctry>FR</Ctry></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id><Nm>Debtor Account 3</Nm></CdtrAcct><RmtInf><Ustrd>My unstructured remittance information</Ustrd></RmtInf></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID4</InstrId><EndToEndId>ENDTOEND4</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">412.34</InstdAmt></Amt><ChrgBr>CRED</ChrgBr><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Debtor 4</Nm><PstlAdr><AdrTp>ADDR</AdrTp><Dept>Dept</Dept><SubDept>SubDept</SubDept><StrtNm>115 rue de Sèvres</StrtNm><PstCd>75006</PstCd><TwnNm>Paris</TwnNm><CtrySubDvsn>IDF</CtrySubDvsn><Ctry>FR</Ctry></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id><Nm>Debtor Account 4</Nm></CdtrAcct><RmtInf><Ustrd>My unstructured remittance information</Ustrd></RmtInf></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID5</InstrId><EndToEndId>ENDTOEND5</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">512.34</InstdAmt></Amt><ChrgBr>CRED</ChrgBr><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Debtor 5</Nm><PstlAdr><AdrTp>ADDR</AdrTp><Dept>Dept</Dept><SubDept>SubDept</SubDept><StrtNm>115 rue de Sèvres</StrtNm><PstCd>75006</PstCd><TwnNm>Paris</TwnNm><CtrySubDvsn>IDF</CtrySubDvsn><Ctry>FR</Ctry></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id><Nm>Debtor Account 5</Nm></CdtrAcct><RmtInf><Ustrd>My unstructured remittance information</Ustrd></RmtInf></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID6</InstrId><EndToEndId>ENDTOEND6</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">612.34</InstdAmt></Amt><ChrgBr>CRED</ChrgBr><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Debtor 6</Nm><PstlAdr><AdrTp>ADDR</AdrTp><Dept>Dept</Dept><SubDept>SubDept</SubDept><StrtNm>115 rue de Sèvres</StrtNm><PstCd>75006</PstCd><TwnNm>Paris</TwnNm><CtrySubDvsn>IDF</CtrySubDvsn><Ctry>FR</Ctry></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id><Nm>Debtor Account 6</Nm></CdtrAcct><RmtInf><Ustrd>My unstructured remittance information</Ustrd></RmtInf></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID7</InstrId><EndToEndId>ENDTOEND7</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">712.34</InstdAmt></Amt><ChrgBr>CRED</ChrgBr><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Debtor 7</Nm><PstlAdr><AdrTp>ADDR</AdrTp><Dept>Dept</Dept><SubDept>SubDept</SubDept><StrtNm>115 rue de Sèvres</StrtNm><PstCd>75006</PstCd><TwnNm>Paris</TwnNm><CtrySubDvsn>IDF</CtrySubDvsn><Ctry>FR</Ctry></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id><Nm>Debtor Account 7</Nm></CdtrAcct><RmtInf><Ustrd>My unstructured remittance information</Ustrd></RmtInf></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID8</InstrId><EndToEndId>ENDTOEND8</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">812.34</InstdAmt></Amt><ChrgBr>CRED</ChrgBr><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Debtor 8</Nm><PstlAdr><AdrTp>ADDR</AdrTp><Dept>Dept</Dept><SubDept>SubDept</SubDept><StrtNm>115 rue de Sèvres</StrtNm><PstCd>75006</PstCd><TwnNm>Paris</TwnNm><CtrySubDvsn>IDF</CtrySubDvsn><Ctry>FR</Ctry></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id><Nm>Debtor Account 8</Nm></CdtrAcct><RmtInf><Ustrd>My unstructured remittance information</Ustrd></RmtInf></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID9</InstrId><EndToEndId>ENDTOEND9</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">912.34</InstdAmt></Amt><ChrgBr>CRED</ChrgBr><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Debtor 9</Nm><PstlAdr><AdrTp>ADDR</AdrTp><Dept>Dept</Dept><SubDept>SubDept</SubDept><StrtNm>115 rue de Sèvres</StrtNm><PstCd>75006</PstCd><TwnNm>Paris</TwnNm><CtrySubDvsn>IDF</CtrySubDvsn><Ctry>FR</Ctry></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id><Nm>Debtor Account 9</Nm></CdtrAcct><RmtInf><Ustrd>My unstructured remittance information</Ustrd></RmtInf></CdtTrfTxInf></PmtInf></CstmrCdtTrfInitn></Document>",
                xml);

        assertValid(xml);
    }

    @Test
    void test_with_account_other_id() {

        final Party debtor = Bank.simpleParty()
                .name("Banque de France")
                .build();
        final BankAccount debtorAccount = Bank.simpleBankAccount()
                .iban("FR7630001007941234567890185")
                .bic("BDFEFRPPXXX")
                .name("Compte de la Banque de France")
                .build();

        Transaction transaction = Bank.simpleTransaction()
                .party(Bank.simpleParty()
                        .name("Debtor")
                        .build())
                .account(Bank.simpleBankAccount()
                        .otherId("1234567890")
                        .bic("PSSTFRPP")
                        .name("Debtor Account")
                        .build())
                .amount("12.34")
                .currency("EUR")
                .endToEndId("ENDTOEND1")
                .id("ID1")
                .build();

        LocalDateTime newYear2021 = LocalDateTime.of(2021, 1, 1, 0, 0, 0, 0);
        LocalDate nextDay = newYear2021.plusDays(1).toLocalDate();

        CreditTransferOperation jaxbCreditTransfer = Bank.jaxbCreditTransferSepa()
                .debtor(debtor)
                .debtorAccount(debtorAccount)
                .transaction(transaction)
                .id("MYID")
                .creationDateTime(newYear2021)
                .requestedExecutionDate(nextDay)
                .build();

        String xml = jaxbCreditTransfer.marshal();

        Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.001.03\"><CstmrCdtTrfInitn><GrpHdr><MsgId>MYID</MsgId><CreDtTm>2021-01-01T00:00:00</CreDtTm><NbOfTxs>1</NbOfTxs><CtrlSum>12.34</CtrlSum><InitgPty><Nm>Banque de France</Nm></InitgPty></GrpHdr><PmtInf><PmtInfId>MYID</PmtInfId><PmtMtd>TRF</PmtMtd><BtchBookg>false</BtchBookg><NbOfTxs>1</NbOfTxs><CtrlSum>12.34</CtrlSum><PmtTpInf><SvcLvl><Cd>SEPA</Cd></SvcLvl></PmtTpInf><ReqdExctnDt>2021-01-02</ReqdExctnDt><Dbtr><Nm>Banque de France</Nm></Dbtr><DbtrAcct><Id><IBAN>FR7630001007941234567890185</IBAN></Id><Nm>Compte de la Banque de France</Nm></DbtrAcct><DbtrAgt><FinInstnId><BIC>BDFEFRPPXXX</BIC></FinInstnId></DbtrAgt><ChrgBr>SLEV</ChrgBr><CdtTrfTxInf><PmtId><InstrId>ID1</InstrId><EndToEndId>ENDTOEND1</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">12.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Debtor</Nm></Cdtr><CdtrAcct><Id><Othr><Id>1234567890</Id></Othr></Id><Nm>Debtor Account</Nm></CdtrAcct></CdtTrfTxInf></PmtInf></CstmrCdtTrfInitn></Document>",
                xml);

        assertValid(xml);
    }

    @Test
    void test_with_unstructured_address() {

        final Party debtor = Bank.simpleParty()
                .name("Banque de France")
                .build();
        final BankAccount debtorAccount = Bank.simpleBankAccount()
                .iban("FR7630001007941234567890185")
                .bic("BDFEFRPPXXX")
                .build();

        List<Transaction> transactions = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            transactions.add(Bank.simpleTransaction()
                    .party(Bank.simpleParty()
                            .name("Beneficiary " + i)
                            .postalAddress(Bank.simplePostalAddress()
                                    .addressLine("115 rue de Sèvres")
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
                    .build());
        }

        LocalDateTime newYear2021 = LocalDateTime.of(2021, 1, 1, 0, 0, 0, 0);
        LocalDate nextDay = newYear2021.plusDays(1).toLocalDate();

        CreditTransferOperation creditTransfer = Bank.jaxbCreditTransferSepa()
                .debtor(debtor)
                .debtorAccount(debtorAccount)
                .transactions(transactions)
                .id("MYID")
                .creationDateTime(newYear2021)
                .requestedExecutionDate(nextDay)
                .build();

        String xml = creditTransfer.marshal();

        Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.001.03\"><CstmrCdtTrfInitn><GrpHdr><MsgId>MYID</MsgId><CreDtTm>2021-01-01T00:00:00</CreDtTm><NbOfTxs>9</NbOfTxs><CtrlSum>4611.06</CtrlSum><InitgPty><Nm>Banque de France</Nm></InitgPty></GrpHdr><PmtInf><PmtInfId>MYID</PmtInfId><PmtMtd>TRF</PmtMtd><BtchBookg>false</BtchBookg><NbOfTxs>9</NbOfTxs><CtrlSum>4611.06</CtrlSum><PmtTpInf><SvcLvl><Cd>SEPA</Cd></SvcLvl></PmtTpInf><ReqdExctnDt>2021-01-02</ReqdExctnDt><Dbtr><Nm>Banque de France</Nm></Dbtr><DbtrAcct><Id><IBAN>FR7630001007941234567890185</IBAN></Id></DbtrAcct><DbtrAgt><FinInstnId><BIC>BDFEFRPPXXX</BIC></FinInstnId></DbtrAgt><ChrgBr>SLEV</ChrgBr><CdtTrfTxInf><PmtId><InstrId>ID1</InstrId><EndToEndId>ENDTOEND1</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">112.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 1</Nm><PstlAdr><Ctry>FR</Ctry><AdrLine>115 rue de Sèvres</AdrLine><AdrLine>75006 Paris</AdrLine></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID2</InstrId><EndToEndId>ENDTOEND2</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">212.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 2</Nm><PstlAdr><Ctry>FR</Ctry><AdrLine>115 rue de Sèvres</AdrLine><AdrLine>75006 Paris</AdrLine></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID3</InstrId><EndToEndId>ENDTOEND3</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">312.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 3</Nm><PstlAdr><Ctry>FR</Ctry><AdrLine>115 rue de Sèvres</AdrLine><AdrLine>75006 Paris</AdrLine></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID4</InstrId><EndToEndId>ENDTOEND4</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">412.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 4</Nm><PstlAdr><Ctry>FR</Ctry><AdrLine>115 rue de Sèvres</AdrLine><AdrLine>75006 Paris</AdrLine></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID5</InstrId><EndToEndId>ENDTOEND5</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">512.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 5</Nm><PstlAdr><Ctry>FR</Ctry><AdrLine>115 rue de Sèvres</AdrLine><AdrLine>75006 Paris</AdrLine></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID6</InstrId><EndToEndId>ENDTOEND6</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">612.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 6</Nm><PstlAdr><Ctry>FR</Ctry><AdrLine>115 rue de Sèvres</AdrLine><AdrLine>75006 Paris</AdrLine></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID7</InstrId><EndToEndId>ENDTOEND7</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">712.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 7</Nm><PstlAdr><Ctry>FR</Ctry><AdrLine>115 rue de Sèvres</AdrLine><AdrLine>75006 Paris</AdrLine></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID8</InstrId><EndToEndId>ENDTOEND8</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">812.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 8</Nm><PstlAdr><Ctry>FR</Ctry><AdrLine>115 rue de Sèvres</AdrLine><AdrLine>75006 Paris</AdrLine></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID9</InstrId><EndToEndId>ENDTOEND9</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">912.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 9</Nm><PstlAdr><Ctry>FR</Ctry><AdrLine>115 rue de Sèvres</AdrLine><AdrLine>75006 Paris</AdrLine></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf></PmtInf></CstmrCdtTrfInitn></Document>",
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
        for (int i = 1; i < 10; i++) {
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

        CreditTransferOperation creditTransfer = Bank.jaxbCreditTransferSepa()
                .debtor(debtor)
                .debtorAccount(debtorAccount)
                .transactions(transactions)
                .creationDateTime(newYear2021)
                .requestedExecutionDate(nextDay)
                .build();

        String xml = creditTransfer.marshal();

        Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.001.03\"><CstmrCdtTrfInitn><GrpHdr><MsgId>20210101120000</MsgId><CreDtTm>2021-01-01T00:00:00</CreDtTm><NbOfTxs>9</NbOfTxs><CtrlSum>4611.06</CtrlSum><InitgPty><Nm>Banque de France</Nm></InitgPty></GrpHdr><PmtInf><PmtInfId>20210101120000</PmtInfId><PmtMtd>TRF</PmtMtd><BtchBookg>false</BtchBookg><NbOfTxs>9</NbOfTxs><CtrlSum>4611.06</CtrlSum><PmtTpInf><SvcLvl><Cd>SEPA</Cd></SvcLvl></PmtTpInf><ReqdExctnDt>2021-01-02</ReqdExctnDt><Dbtr><Nm>Banque de France</Nm></Dbtr><DbtrAcct><Id><IBAN>FR7630001007941234567890185</IBAN></Id></DbtrAcct><DbtrAgt><FinInstnId/></DbtrAgt><ChrgBr>SLEV</ChrgBr><CdtTrfTxInf><PmtId><EndToEndId>ENDTOEND1</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">112.34</InstdAmt></Amt><Cdtr><Nm>Beneficiary 1</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><EndToEndId>ENDTOEND2</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">212.34</InstdAmt></Amt><Cdtr><Nm>Beneficiary 2</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><EndToEndId>ENDTOEND3</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">312.34</InstdAmt></Amt><Cdtr><Nm>Beneficiary 3</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><EndToEndId>ENDTOEND4</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">412.34</InstdAmt></Amt><Cdtr><Nm>Beneficiary 4</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><EndToEndId>ENDTOEND5</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">512.34</InstdAmt></Amt><Cdtr><Nm>Beneficiary 5</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><EndToEndId>ENDTOEND6</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">612.34</InstdAmt></Amt><Cdtr><Nm>Beneficiary 6</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><EndToEndId>ENDTOEND7</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">712.34</InstdAmt></Amt><Cdtr><Nm>Beneficiary 7</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><EndToEndId>ENDTOEND8</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">812.34</InstdAmt></Amt><Cdtr><Nm>Beneficiary 8</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><EndToEndId>ENDTOEND9</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">912.34</InstdAmt></Amt><Cdtr><Nm>Beneficiary 9</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf></PmtInf></CstmrCdtTrfInitn></Document>",
                xml);

        assertValid(xml);
    }

    @Test
    void test_instant_payment_is_not_allowed_for_v03() {
        var creditTransferOperationBuilder = Bank.jaxbCreditTransferSepa();
        Assertions.assertThrows(IllegalStateException.class, () -> creditTransferOperationBuilder.instantPayment(true),
        "Instant payment is not allowed for pain.001.001.03");
    }

    private void assertValid(String xml) {
        Assertions.assertDoesNotThrow(() -> SchemaValidator.validateCreditTransfer(V03, new StringReader(xml)));
    }
}
