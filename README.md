[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

# bank4j

Easily generate XML Credit Transfers based on [ISO 20022](https://www.iso20022.org/) Payments Initiation `pain.001.001.03`.

Provides IBAN and BIC validation with annotations.

Written in Java 8 using JAXB and [iban4j](https://github.com/arturmkrtchyan/iban4j).

Tested with [Qonto](https://qonto.com) bulk SEPA transfers.

## Installation

```xml

<dependency>
    <groupId>io.inisos.bank4j</groupId>
    <artifactId>bank4j</artifactId>
    <version>1.2.0</version>
</dependency>
```

## Usage

### IBAN and BIC validation

```java
class MyRecord {
    
    @IBAN
    private String iban;

    @BIC
    private String bic;
    
}
```

Only accepts valid IBAN, BIC8 and BIC11.

### Bulk SEPA transfers

Simply provide bank account details and transactions:

```java
class MyApp {

    public static void main(String...args) {

        // debtor
        BankAccount debtor = new SimpleBankAccount(
                "Debtor Account Name",         // Name
                "FR7610011000201234567890188", // IBAN
                "PSSTFRPP"                     // BIC (optional)
        );
        // creditor
        BankAccount creditor = new SimpleBankAccount(
                "Beneficiary Account Name",    // Name
                "FR7630001007941234567890185", // IBAN
                "BDFEFRPP"                     // BIC (optional)
        );
        // transactions
        Transaction transaction1 = Bank.simpleTransaction()
                .thirdParty(creditor)               // Beneficiary bank account
                .amount("12.34")                    // Amount, converted to BigDecimal
                .currency("EUR")                    // Currency code
                .endToEndId("Transfer reference 1") // End to end identifier
                .id("Optional identifier 1")        // Transaction identifier (optional)
                .build();
        Transaction transaction2 = Bank.simpleTransaction()
                .thirdParty(creditor)               // Beneficiary bank account
                .amount(new BigDecimal("56.78"))    // Amount as BigDecimal
                .currency("EUR")                    // Currency code
                .endToEndId("Transfer reference 2") // End to end identifier
                .id("Optional identifier 2")        // Transaction identifier (optional)
                .build();

        // transfer
        CreditTransferOperation creditTransfer = Bank.jaxbCreditTransferSepa()
                .debtor(debtor)                                      // mandatory debtor
                .transaction(transaction1)                           // at least 1 transaction
                .transaction(transaction2)                           // optional additional transaction
                .creationDateTime(LocalDateTime.now())               // optional message creation date and time, defaults to now
                .requestedExecutionDate(LocalDate.now().plusDays(1)) // optional requested execution date, defaults to tomorrow
                .id("MYID")                                          // optional identifier, defaults to reation date and time as yyyyMMddhhmmss
                .build();

        // export to string
        String formattedOutput = creditTransfer.marshal(true); // true: enables formatting

        // or export to file
        creditTransfer.marshal(new FileWriter("myFile.xml")); // default: disables formatting
    }
}
```

Output with formatting:

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Document xmlns="urn:iso:std:iso:20022:tech:xsd:pain.001.001.03">
    <CstmrCdtTrfInitn>
        <GrpHdr>
            <MsgId>20210104054241</MsgId>
            <CreDtTm>2021-01-04T05:42:41.7338511</CreDtTm>
            <NbOfTxs>2</NbOfTxs>
            <CtrlSum>69.12</CtrlSum>
            <InitgPty>
                <Nm>Debtor Account Name</Nm>
            </InitgPty>
        </GrpHdr>
        <PmtInf>
            <PmtInfId>MYID</PmtInfId>
            <PmtMtd>TRF</PmtMtd>
            <BtchBookg>false</BtchBookg>
            <NbOfTxs>2</NbOfTxs>
            <CtrlSum>69.12</CtrlSum>
            <PmtTpInf>
                <SvcLvl>
                    <Cd>SEPA</Cd>
                </SvcLvl>
            </PmtTpInf>
            <ReqdExctnDt>2021-01-05</ReqdExctnDt>
            <Dbtr>
                <Nm>Debtor Account Name</Nm>
            </Dbtr>
            <DbtrAcct>
                <Id>
                    <IBAN>FR7610011000201234567890188</IBAN>
                </Id>
            </DbtrAcct>
            <DbtrAgt>
                <FinInstnId>
                    <BIC>PSSTFRPP</BIC>
                </FinInstnId>
            </DbtrAgt>
            <ChrgBr>SLEV</ChrgBr>
            <CdtTrfTxInf>
                <PmtId>
                    <InstrId>Optional identifier 1</InstrId>
                    <EndToEndId>Transfer reference 1</EndToEndId>
                </PmtId>
                <Amt>
                    <InstdAmt Ccy="EUR">12.34</InstdAmt>
                </Amt>
                <CdtrAgt>
                    <FinInstnId>
                        <BIC>BDFEFRPP</BIC>
                    </FinInstnId>
                </CdtrAgt>
                <Cdtr>
                    <Nm>Beneficiary Account Name</Nm>
                </Cdtr>
                <CdtrAcct>
                    <Id>
                        <IBAN>FR7630001007941234567890185</IBAN>
                    </Id>
                </CdtrAcct>
            </CdtTrfTxInf>
            <CdtTrfTxInf>
                <PmtId>
                    <InstrId>Optional identifier 2</InstrId>
                    <EndToEndId>Transfer reference 2</EndToEndId>
                </PmtId>
                <Amt>
                    <InstdAmt Ccy="EUR">56.78</InstdAmt>
                </Amt>
                <CdtrAgt>
                    <FinInstnId>
                        <BIC>BDFEFRPP</BIC>
                    </FinInstnId>
                </CdtrAgt>
                <Cdtr>
                    <Nm>Beneficiary Account Name</Nm>
                </Cdtr>
                <CdtrAcct>
                    <Id>
                        <IBAN>FR7630001007941234567890185</IBAN>
                    </Id>
                </CdtrAcct>
            </CdtTrfTxInf>
        </PmtInf>
    </CstmrCdtTrfInitn>
</Document>
```

## Interfaces

You can use your own implementations of `Transaction` and `BankAccount` but simple defaults are provided.
