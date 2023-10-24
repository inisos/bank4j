package io.inisos.bank4j.jaxb2;

import io.inisos.bank4j.BankAccount;
import io.inisos.bank4j.Builders;
import io.inisos.bank4j.Party;
import io.inisos.bank4j.Transaction;
import io.inisos.bank4j.simple.SimpleBuilders;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrice Blanchardie
 */
class JAXB2CreditTransferTest {

    private static final Builders builders = SimpleBuilders.get();

    @Test
    void test_with_all_fields() {

        final Party debtor = builders.party()
                .name("Banque de France")
                .build();
        final BankAccount debtorAccount = builders.bankAccount()
                .iban("FR7630001007941234567890185")
                .bic("BDFEFRPPXXX")
                .name("Compte de la Banque de France")
                .build();

        List<Transaction> transactions = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            transactions.add(builders.transaction()
                    .party(builders.party()
                            .name("Debtor " + i)
                            .postalAddress(builders.postalAddress()
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
                    .account(builders.bankAccount()
                            .iban("FR7610011000201234567890188")
                            .bic("PSSTFRPP")
                            .name("Debtor Account " + i)
                            .build())
                    .amount(i + "12.34")
                    .currency("EUR")
                    .endToEndId("ENDTOEND" + i)
                    .id("ID" + i)
                    .build());
        }

        LocalDateTime newYear2021 = LocalDateTime.of(2021, 1, 1, 0, 0, 0, 0);
        LocalDate nextDay = newYear2021.plusDays(1).toLocalDate();

        JAXB2CreditTransfer jaxb2CreditTransfer = new JAXB2CreditTransfer(builders.creditTransferSepa()
                .debtor(debtor)
                .debtorAccount(debtorAccount)
                .transactions(transactions)
                .id("MYID")
                .creationDateTime(newYear2021)
                .requestedExecutionDate(nextDay)
                .build());

        Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.001.03\"><CstmrCdtTrfInitn><GrpHdr><MsgId>MYID</MsgId><CreDtTm>2021-01-01T00:00:00</CreDtTm><NbOfTxs>9</NbOfTxs><CtrlSum>4611.06</CtrlSum><InitgPty><Nm>Banque de France</Nm></InitgPty></GrpHdr><PmtInf><PmtInfId>MYID</PmtInfId><PmtMtd>TRF</PmtMtd><BtchBookg>false</BtchBookg><NbOfTxs>9</NbOfTxs><CtrlSum>4611.06</CtrlSum><PmtTpInf><SvcLvl><Cd>SEPA</Cd></SvcLvl></PmtTpInf><ReqdExctnDt>2021-01-02</ReqdExctnDt><Dbtr><Nm>Banque de France</Nm></Dbtr><DbtrAcct><Id><IBAN>FR7630001007941234567890185</IBAN></Id><Nm>Compte de la Banque de France</Nm></DbtrAcct><DbtrAgt><FinInstnId><BIC>BDFEFRPPXXX</BIC></FinInstnId></DbtrAgt><ChrgBr>SLEV</ChrgBr><CdtTrfTxInf><PmtId><InstrId>ID1</InstrId><EndToEndId>ENDTOEND1</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">112.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Debtor 1</Nm><PstlAdr><AdrTp>ADDR</AdrTp><Dept>Dept</Dept><SubDept>SubDept</SubDept><StrtNm>115 rue de Sèvres</StrtNm><PstCd>75006</PstCd><TwnNm>Paris</TwnNm><CtrySubDvsn>IDF</CtrySubDvsn><Ctry>FR</Ctry></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id><Nm>Debtor Account 1</Nm></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID2</InstrId><EndToEndId>ENDTOEND2</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">212.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Debtor 2</Nm><PstlAdr><AdrTp>ADDR</AdrTp><Dept>Dept</Dept><SubDept>SubDept</SubDept><StrtNm>115 rue de Sèvres</StrtNm><PstCd>75006</PstCd><TwnNm>Paris</TwnNm><CtrySubDvsn>IDF</CtrySubDvsn><Ctry>FR</Ctry></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id><Nm>Debtor Account 2</Nm></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID3</InstrId><EndToEndId>ENDTOEND3</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">312.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Debtor 3</Nm><PstlAdr><AdrTp>ADDR</AdrTp><Dept>Dept</Dept><SubDept>SubDept</SubDept><StrtNm>115 rue de Sèvres</StrtNm><PstCd>75006</PstCd><TwnNm>Paris</TwnNm><CtrySubDvsn>IDF</CtrySubDvsn><Ctry>FR</Ctry></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id><Nm>Debtor Account 3</Nm></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID4</InstrId><EndToEndId>ENDTOEND4</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">412.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Debtor 4</Nm><PstlAdr><AdrTp>ADDR</AdrTp><Dept>Dept</Dept><SubDept>SubDept</SubDept><StrtNm>115 rue de Sèvres</StrtNm><PstCd>75006</PstCd><TwnNm>Paris</TwnNm><CtrySubDvsn>IDF</CtrySubDvsn><Ctry>FR</Ctry></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id><Nm>Debtor Account 4</Nm></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID5</InstrId><EndToEndId>ENDTOEND5</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">512.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Debtor 5</Nm><PstlAdr><AdrTp>ADDR</AdrTp><Dept>Dept</Dept><SubDept>SubDept</SubDept><StrtNm>115 rue de Sèvres</StrtNm><PstCd>75006</PstCd><TwnNm>Paris</TwnNm><CtrySubDvsn>IDF</CtrySubDvsn><Ctry>FR</Ctry></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id><Nm>Debtor Account 5</Nm></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID6</InstrId><EndToEndId>ENDTOEND6</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">612.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Debtor 6</Nm><PstlAdr><AdrTp>ADDR</AdrTp><Dept>Dept</Dept><SubDept>SubDept</SubDept><StrtNm>115 rue de Sèvres</StrtNm><PstCd>75006</PstCd><TwnNm>Paris</TwnNm><CtrySubDvsn>IDF</CtrySubDvsn><Ctry>FR</Ctry></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id><Nm>Debtor Account 6</Nm></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID7</InstrId><EndToEndId>ENDTOEND7</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">712.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Debtor 7</Nm><PstlAdr><AdrTp>ADDR</AdrTp><Dept>Dept</Dept><SubDept>SubDept</SubDept><StrtNm>115 rue de Sèvres</StrtNm><PstCd>75006</PstCd><TwnNm>Paris</TwnNm><CtrySubDvsn>IDF</CtrySubDvsn><Ctry>FR</Ctry></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id><Nm>Debtor Account 7</Nm></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID8</InstrId><EndToEndId>ENDTOEND8</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">812.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Debtor 8</Nm><PstlAdr><AdrTp>ADDR</AdrTp><Dept>Dept</Dept><SubDept>SubDept</SubDept><StrtNm>115 rue de Sèvres</StrtNm><PstCd>75006</PstCd><TwnNm>Paris</TwnNm><CtrySubDvsn>IDF</CtrySubDvsn><Ctry>FR</Ctry></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id><Nm>Debtor Account 8</Nm></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID9</InstrId><EndToEndId>ENDTOEND9</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">912.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Debtor 9</Nm><PstlAdr><AdrTp>ADDR</AdrTp><Dept>Dept</Dept><SubDept>SubDept</SubDept><StrtNm>115 rue de Sèvres</StrtNm><PstCd>75006</PstCd><TwnNm>Paris</TwnNm><CtrySubDvsn>IDF</CtrySubDvsn><Ctry>FR</Ctry></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id><Nm>Debtor Account 9</Nm></CdtrAcct></CdtTrfTxInf></PmtInf></CstmrCdtTrfInitn></Document>",
                jaxb2CreditTransfer.marshal());
    }

    @Test
    void test_with_unstructured_address() {

        final Party debtor = builders.party()
                .name("Banque de France")
                .build();
        final BankAccount debtorAccount = builders.bankAccount()
                .iban("FR7630001007941234567890185")
                .bic("BDFEFRPPXXX")
                .build();

        List<Transaction> transactions = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            transactions.add(builders.transaction()
                    .party(builders.party()
                            .name("Beneficiary " + i)
                            .postalAddress(builders.postalAddress()
                                    .addressLine("115 rue de Sèvres")
                                    .addressLine("75006 Paris")
                                    .country("FR")
                                    .build())
                            .build())
                    .account(builders.bankAccount()
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

        JAXB2CreditTransfer jaxb2CreditTransfer = new JAXB2CreditTransfer(builders.creditTransferSepa()
                .debtor(debtor)
                .debtorAccount(debtorAccount)
                .transactions(transactions)
                .id("MYID")
                .creationDateTime(newYear2021)
                .requestedExecutionDate(nextDay)
                .build());

        Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.001.03\"><CstmrCdtTrfInitn><GrpHdr><MsgId>MYID</MsgId><CreDtTm>2021-01-01T00:00:00</CreDtTm><NbOfTxs>9</NbOfTxs><CtrlSum>4611.06</CtrlSum><InitgPty><Nm>Banque de France</Nm></InitgPty></GrpHdr><PmtInf><PmtInfId>MYID</PmtInfId><PmtMtd>TRF</PmtMtd><BtchBookg>false</BtchBookg><NbOfTxs>9</NbOfTxs><CtrlSum>4611.06</CtrlSum><PmtTpInf><SvcLvl><Cd>SEPA</Cd></SvcLvl></PmtTpInf><ReqdExctnDt>2021-01-02</ReqdExctnDt><Dbtr><Nm>Banque de France</Nm></Dbtr><DbtrAcct><Id><IBAN>FR7630001007941234567890185</IBAN></Id></DbtrAcct><DbtrAgt><FinInstnId><BIC>BDFEFRPPXXX</BIC></FinInstnId></DbtrAgt><ChrgBr>SLEV</ChrgBr><CdtTrfTxInf><PmtId><InstrId>ID1</InstrId><EndToEndId>ENDTOEND1</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">112.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 1</Nm><PstlAdr><Ctry>FR</Ctry><AdrLine>115 rue de Sèvres</AdrLine><AdrLine>75006 Paris</AdrLine></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID2</InstrId><EndToEndId>ENDTOEND2</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">212.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 2</Nm><PstlAdr><Ctry>FR</Ctry><AdrLine>115 rue de Sèvres</AdrLine><AdrLine>75006 Paris</AdrLine></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID3</InstrId><EndToEndId>ENDTOEND3</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">312.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 3</Nm><PstlAdr><Ctry>FR</Ctry><AdrLine>115 rue de Sèvres</AdrLine><AdrLine>75006 Paris</AdrLine></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID4</InstrId><EndToEndId>ENDTOEND4</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">412.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 4</Nm><PstlAdr><Ctry>FR</Ctry><AdrLine>115 rue de Sèvres</AdrLine><AdrLine>75006 Paris</AdrLine></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID5</InstrId><EndToEndId>ENDTOEND5</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">512.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 5</Nm><PstlAdr><Ctry>FR</Ctry><AdrLine>115 rue de Sèvres</AdrLine><AdrLine>75006 Paris</AdrLine></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID6</InstrId><EndToEndId>ENDTOEND6</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">612.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 6</Nm><PstlAdr><Ctry>FR</Ctry><AdrLine>115 rue de Sèvres</AdrLine><AdrLine>75006 Paris</AdrLine></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID7</InstrId><EndToEndId>ENDTOEND7</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">712.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 7</Nm><PstlAdr><Ctry>FR</Ctry><AdrLine>115 rue de Sèvres</AdrLine><AdrLine>75006 Paris</AdrLine></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID8</InstrId><EndToEndId>ENDTOEND8</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">812.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 8</Nm><PstlAdr><Ctry>FR</Ctry><AdrLine>115 rue de Sèvres</AdrLine><AdrLine>75006 Paris</AdrLine></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID9</InstrId><EndToEndId>ENDTOEND9</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">912.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 9</Nm><PstlAdr><Ctry>FR</Ctry><AdrLine>115 rue de Sèvres</AdrLine><AdrLine>75006 Paris</AdrLine></PstlAdr></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf></PmtInf></CstmrCdtTrfInitn></Document>",
                jaxb2CreditTransfer.marshal());
    }

    @Test
    void test_without_optional_fields() {

        final Party debtor = builders.party()
                .name("Banque de France")
                .build();
        final BankAccount debtorAccount = builders.bankAccount()
                .iban("FR7630001007941234567890185")
                .build();

        List<Transaction> transactions = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            transactions.add(builders.transaction()
                    .party(builders.party()
                            .name("Beneficiary " + i)
                            .build())
                    .account(builders.bankAccount()
                            .iban("FR7610011000201234567890188")
                            .build())
                    .amount(i + "12.34")
                    .currency("EUR")
                    .endToEndId("ENDTOEND" + i)
                    .build());
        }

        LocalDateTime newYear2021 = LocalDateTime.of(2021, 1, 1, 0, 0, 0, 0);
        LocalDate nextDay = newYear2021.plusDays(1).toLocalDate();

        JAXB2CreditTransfer jaxb2CreditTransfer = new JAXB2CreditTransfer(builders.creditTransferSepa()
                .debtor(debtor)
                .debtorAccount(debtorAccount)
                .transactions(transactions)
                .creationDateTime(newYear2021)
                .requestedExecutionDate(nextDay)
                .build());

        Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.001.03\"><CstmrCdtTrfInitn><GrpHdr><MsgId>20210101120000</MsgId><CreDtTm>2021-01-01T00:00:00</CreDtTm><NbOfTxs>9</NbOfTxs><CtrlSum>4611.06</CtrlSum><InitgPty><Nm>Banque de France</Nm></InitgPty></GrpHdr><PmtInf><PmtInfId>20210101120000</PmtInfId><PmtMtd>TRF</PmtMtd><BtchBookg>false</BtchBookg><NbOfTxs>9</NbOfTxs><CtrlSum>4611.06</CtrlSum><PmtTpInf><SvcLvl><Cd>SEPA</Cd></SvcLvl></PmtTpInf><ReqdExctnDt>2021-01-02</ReqdExctnDt><Dbtr><Nm>Banque de France</Nm></Dbtr><DbtrAcct><Id><IBAN>FR7630001007941234567890185</IBAN></Id></DbtrAcct><ChrgBr>SLEV</ChrgBr><CdtTrfTxInf><PmtId><EndToEndId>ENDTOEND1</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">112.34</InstdAmt></Amt><Cdtr><Nm>Beneficiary 1</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><EndToEndId>ENDTOEND2</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">212.34</InstdAmt></Amt><Cdtr><Nm>Beneficiary 2</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><EndToEndId>ENDTOEND3</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">312.34</InstdAmt></Amt><Cdtr><Nm>Beneficiary 3</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><EndToEndId>ENDTOEND4</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">412.34</InstdAmt></Amt><Cdtr><Nm>Beneficiary 4</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><EndToEndId>ENDTOEND5</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">512.34</InstdAmt></Amt><Cdtr><Nm>Beneficiary 5</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><EndToEndId>ENDTOEND6</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">612.34</InstdAmt></Amt><Cdtr><Nm>Beneficiary 6</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><EndToEndId>ENDTOEND7</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">712.34</InstdAmt></Amt><Cdtr><Nm>Beneficiary 7</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><EndToEndId>ENDTOEND8</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">812.34</InstdAmt></Amt><Cdtr><Nm>Beneficiary 8</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><EndToEndId>ENDTOEND9</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">912.34</InstdAmt></Amt><Cdtr><Nm>Beneficiary 9</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf></PmtInf></CstmrCdtTrfInitn></Document>",
                jaxb2CreditTransfer.marshal());
    }
}
