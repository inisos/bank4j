package io.inisos.bank4j;

import io.inisos.bank4j.impl.SimpleBankAccount;
import io.inisos.bank4j.impl.SimpleTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrice Blanchardie
 */
class CreditTransferTest {

    @Test
    void test_with_all_fields() {

        final BankAccount sender = new SimpleBankAccount(
                "Banque de France",
                "FR7630001007941234567890185",
                "BDFEFRPPXXX"
        );

        List<Transaction> transactions = new ArrayList<>();
        for (int i = 1; i < 10; i++)
            transactions.add(new SimpleTransaction(
                    new SimpleBankAccount(
                            "Beneficiary " + i,
                            "FR7610011000201234567890188",
                            "PSSTFRPP"
                    ),
                    new BigDecimal(i + "12.34"),
                    "EUR",
                    "ENDTOEND" + i,
                    "ID" + i));

        LocalDateTime newYear2021 = LocalDateTime.of(2021, 1, 1, 0, 0, 0, 0);
        LocalDate nextDay = newYear2021.plusDays(1).toLocalDate();

        CreditTransfer creditTransfer = Bank.creditTransferSepa(sender, transactions, "MYID", newYear2021, nextDay);

        System.out.println(creditTransfer.marshal(false));

        Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.001.03\"><CstmrCdtTrfInitn><GrpHdr><MsgId>MYID</MsgId><CreDtTm>2021-01-01T00:00:00</CreDtTm><NbOfTxs>9</NbOfTxs><CtrlSum>4611.06</CtrlSum><InitgPty><Nm>Banque de France</Nm></InitgPty></GrpHdr><PmtInf><PmtInfId>MYID</PmtInfId><PmtMtd>TRF</PmtMtd><BtchBookg>false</BtchBookg><NbOfTxs>9</NbOfTxs><CtrlSum>4611.06</CtrlSum><PmtTpInf><SvcLvl><Cd>SEPA</Cd></SvcLvl></PmtTpInf><ReqdExctnDt>2021-01-02</ReqdExctnDt><Dbtr><Nm>Banque de France</Nm></Dbtr><DbtrAcct><Id><IBAN>FR7630001007941234567890185</IBAN></Id></DbtrAcct><DbtrAgt><FinInstnId><BIC>BDFEFRPPXXX</BIC></FinInstnId></DbtrAgt><ChrgBr>SLEV</ChrgBr><CdtTrfTxInf><PmtId><InstrId>ID1</InstrId><EndToEndId>ENDTOEND1</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">112.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 1</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID2</InstrId><EndToEndId>ENDTOEND2</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">212.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 2</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID3</InstrId><EndToEndId>ENDTOEND3</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">312.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 3</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID4</InstrId><EndToEndId>ENDTOEND4</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">412.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 4</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID5</InstrId><EndToEndId>ENDTOEND5</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">512.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 5</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID6</InstrId><EndToEndId>ENDTOEND6</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">612.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 6</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID7</InstrId><EndToEndId>ENDTOEND7</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">712.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 7</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID8</InstrId><EndToEndId>ENDTOEND8</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">812.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 8</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><InstrId>ID9</InstrId><EndToEndId>ENDTOEND9</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">912.34</InstdAmt></Amt><CdtrAgt><FinInstnId><BIC>PSSTFRPP</BIC></FinInstnId></CdtrAgt><Cdtr><Nm>Beneficiary 9</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf></PmtInf></CstmrCdtTrfInitn></Document>",
                creditTransfer.marshal());
    }

    @Test
    void test_without_optional_fields() {

        final BankAccount sender = new SimpleBankAccount(
                "Banque de France",
                "FR7630001007941234567890185"
        );

        List<Transaction> transactions = new ArrayList<>();
        for (int i = 1; i < 10; i++)
            transactions.add(new SimpleTransaction(
                    new SimpleBankAccount(
                            "Beneficiary " + i,
                            "FR7610011000201234567890188"
                    ),
                    new BigDecimal(i + "12.34"),
                    "EUR",
                    "ENDTOEND" + i,
                    null));

        LocalDateTime newYear2021 = LocalDateTime.of(2021, 1, 1, 0, 0, 0, 0);
        LocalDate nextDay = newYear2021.plusDays(1).toLocalDate();

        CreditTransfer creditTransfer = Bank.creditTransferSepa(sender, transactions, newYear2021, nextDay);

        System.out.println(creditTransfer.marshal(false));

        Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.001.03\"><CstmrCdtTrfInitn><GrpHdr><MsgId>20210101120000</MsgId><CreDtTm>2021-01-01T00:00:00</CreDtTm><NbOfTxs>9</NbOfTxs><CtrlSum>4611.06</CtrlSum><InitgPty><Nm>Banque de France</Nm></InitgPty></GrpHdr><PmtInf><PmtInfId>20210101120000</PmtInfId><PmtMtd>TRF</PmtMtd><BtchBookg>false</BtchBookg><NbOfTxs>9</NbOfTxs><CtrlSum>4611.06</CtrlSum><PmtTpInf><SvcLvl><Cd>SEPA</Cd></SvcLvl></PmtTpInf><ReqdExctnDt>2021-01-02</ReqdExctnDt><Dbtr><Nm>Banque de France</Nm></Dbtr><DbtrAcct><Id><IBAN>FR7630001007941234567890185</IBAN></Id></DbtrAcct><ChrgBr>SLEV</ChrgBr><CdtTrfTxInf><PmtId><EndToEndId>ENDTOEND1</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">112.34</InstdAmt></Amt><Cdtr><Nm>Beneficiary 1</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><EndToEndId>ENDTOEND2</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">212.34</InstdAmt></Amt><Cdtr><Nm>Beneficiary 2</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><EndToEndId>ENDTOEND3</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">312.34</InstdAmt></Amt><Cdtr><Nm>Beneficiary 3</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><EndToEndId>ENDTOEND4</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">412.34</InstdAmt></Amt><Cdtr><Nm>Beneficiary 4</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><EndToEndId>ENDTOEND5</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">512.34</InstdAmt></Amt><Cdtr><Nm>Beneficiary 5</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><EndToEndId>ENDTOEND6</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">612.34</InstdAmt></Amt><Cdtr><Nm>Beneficiary 6</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><EndToEndId>ENDTOEND7</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">712.34</InstdAmt></Amt><Cdtr><Nm>Beneficiary 7</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><EndToEndId>ENDTOEND8</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">812.34</InstdAmt></Amt><Cdtr><Nm>Beneficiary 8</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf><CdtTrfTxInf><PmtId><EndToEndId>ENDTOEND9</EndToEndId></PmtId><Amt><InstdAmt Ccy=\"EUR\">912.34</InstdAmt></Amt><Cdtr><Nm>Beneficiary 9</Nm></Cdtr><CdtrAcct><Id><IBAN>FR7610011000201234567890188</IBAN></Id></CdtrAcct></CdtTrfTxInf></PmtInf></CstmrCdtTrfInitn></Document>",
                creditTransfer.marshal());
    }
}
