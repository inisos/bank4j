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
    <version>2.2.0-SNAPSHOT</version>
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
import io.inisos.bank4j.Bank;

class MyApp {

    public static void main(String... args) {

        // Optional debtor identification
        Party debtor = Bank.simpleParty()
                .name("Debtor Name") // Optional name
                .build();

        // Debtor account
        BankAccount debtorAccount = Bank.simpleBankAccount()
                .iban("FR7610011000201234567890188") // IBAN
                .bic("PSSTFRPP")                     // Optional BIC
                .build();

        // Transactions
        Transaction transaction1 = Bank.simpleTransaction()
                .party(Bank.simpleParty()           // Optional creditor identification
                        .name("BANQUE DE FRANCE")                 // Optional name
                        .postalAddress(Bank.simplePostalAddress() // Optional postal address
                                .addressLine("1, rue de La Vrillière")
                                .addressLine("75001 PARIS")
                                .country("FR")
                                .build())
                        .build())
                .account(Bank.simpleBankAccount()   // Creditor account
                        .iban("FR7630001007941234567890185") // IBAN
                        .bic("BDFEFRPP")                     // Optional BIC
                        .build())           
                .amount("12.34")                    // Amount, converted to BigDecimal
                .currency("EUR")                    // Currency code
                .endToEndId("Transfer reference 1") // End to end identifier
                .id("Optional identifier 1")        // Optional Transaction identifier
                .build();
        Transaction transaction2 = Bank.simpleTransaction()
                .party(Bank.simpleParty()           // Optional creditor identification
                        .name("Creditor Name")                    // Optional name
                        .build())                    
                .account(Bank.simpleBankAccount()   // Creditor account
                        .otherId("1234567890")               // Other identification
                        .bic("BDFEFRPP")                     // BIC
                        .build())           // Creditor account
                .amount(new BigDecimal("56.78"))    // Amount as BigDecimal
                .currency("EUR")                    // Currency code
                .endToEndId("Transfer reference 2") // End to end identifier
                .id("Optional identifier 2")        // Optional transaction identifier
                .build();

        // Transfer
        CreditTransferOperation creditTransfer = Bank.jaxbCreditTransferSepa()
                .debtor(debtor)                                      // Optional debtor
                .debtorAccount(debtorAccount)                        // Mandatory debtor account
                .transaction(transaction1)                           // At least 1 transaction
                .transaction(transaction2)                           // Optional additional transaction
                .creationDateTime(LocalDateTime.now())               // Optional message creation date and time, defaults to now
                .requestedExecutionDate(LocalDate.now().plusDays(1)) // Optional requested execution date, defaults to tomorrow
                .id("MYID")                                          // Optional identifier, defaults to creation date and time as yyyyMMddhhmmss
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
            <MsgId>MYID</MsgId>
            <CreDtTm>2023-10-25T09:47:05.187</CreDtTm>
            <NbOfTxs>2</NbOfTxs>
            <CtrlSum>69.12</CtrlSum>
            <InitgPty>
                <Nm>Debtor Name</Nm>
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
            <ReqdExctnDt>2023-10-26</ReqdExctnDt>
            <Dbtr>
                <Nm>Debtor Name</Nm>
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
                    <Nm>BANQUE DE FRANCE</Nm>
                    <PstlAdr>
                        <Ctry>FR</Ctry>
                        <AdrLine>1, rue de La Vrillière</AdrLine>
                        <AdrLine>75001 PARIS</AdrLine>
                    </PstlAdr>
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
                    <Nm>Creditor Name</Nm>
                </Cdtr>
                <CdtrAcct>
                    <Id>
                        <Othr>
                            <Id>1234567890</Id>
                        </Othr>
                    </Id>
                </CdtrAcct>
            </CdtTrfTxInf>
        </PmtInf>
    </CstmrCdtTrfInitn>
</Document>
```

## Interfaces

You can use your own implementations of `Transaction` and `BankAccount` but simple defaults are provided.
