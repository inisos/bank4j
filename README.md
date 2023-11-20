[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

# bank4j

Easily generate XML Credit Transfers based on [ISO 20022](https://www.iso20022.org/) Payments Initiation `pain.001.001.03`.

Provides IBAN and BIC validation with annotations.

Written in Java 8 using JAXB and [iban4j](https://github.com/arturmkrtchyan/iban4j).

Tested with [Qonto](https://qonto.com) bulk SEPA transfers.

## Installation

### Requirements

Java 8+

For validation, you need to provide a Jakarta Bean Validation implementation (e.g. [Hibernate Validator](https://hibernate.org/validator/)).

### Full installation

Bundles API, JAXB2 implementation, validation based on Jakarta Bean Validation 2 and a simple data model with builders.

```xml
<dependency>
    <groupId>io.inisos.bank4j</groupId>
    <artifactId>bank4j</artifactId>
    <version>3.0.0-SNAPSHOT</version>
</dependency>
```

In the future, this artifact may switch to latest versions (JAXB 3+, Jakarta Bean Validation 3+, etc.).

### Custom installation

#### API

```xml

<dependencies>
    <dependency>
        <groupId>io.inisos.bank4j</groupId>
        <artifactId>bank4j-api</artifactId>
        <version>3.0.0-SNAPSHOT</version>
    </dependency>
    <!-- if you want validation, provide your Jakarta Bean Validation implementation,
         e.g. Hibernate Validator -->
    <dependency>
        <groupId>org.hibernate.validator</groupId>
        <artifactId>hibernate-validator</artifactId>
        <version>6.2.5.Final</version>
    </dependency>
</dependencies>
```

#### XML message generation using JAXB 2

Depends on `javax.xml.bind` package.

```xml

<dependency>
    <groupId>io.inisos.bank4j</groupId>
    <artifactId>bank4j-jaxb2</artifactId>
    <version>3.0.0-SNAPSHOT</version>
</dependency>
```

Glassfish `jaxb-runtime` implementation is a transitive dependency with runtime scope. Feel free to exclude it if you already use another implementation.

#### Simple data model with builders

You can use your own implementations, but simple defaults are provided for convenience.

```xml

<dependency>
    <groupId>io.inisos.bank4j</groupId>
    <artifactId>bank4j-simple</artifactId>
    <version>3.0.0-SNAPSHOT</version>
</dependency>
```

#### Validation based on Jakarta Bean Validation 2.x

Depends on `javax.validation` package.

```xml

<dependencies>
    <dependency>
        <groupId>io.inisos.bank4j</groupId>
        <artifactId>bank4j-validation2</artifactId>
        <version>3.0.0-SNAPSHOT</version>
    </dependency>
    <!-- your Jakarta Bean Validation implementation, e.g. Hibernate Validator -->
    <dependency>
        <groupId>org.hibernate.validator</groupId>
        <artifactId>hibernate-validator</artifactId>
        <version>6.2.5.Final</version>
    </dependency>
</dependencies>
```

## Usage

### IBAN and BIC validation

Provided by bank4j-validation*

```java
class MyRecord {
    
    @IBAN
    private String iban;

    @BIC
    private String bic;
    
}
```

Only accepts valid IBAN, BIC8 and BIC11.

### XML message (pain) generation

Simply provide bank account details and transactions:

```java
class MyApp {

    public static void main(String... args) {

        Builders builders = SimpleBuilders.get();

        // Optional debtor identification
        Party debtor = builders.party()
                .name("Debtor Name") // Optional name
                .build();

        // Debtor account
        BankAccount debtorAccount = builders.bankAccount()
                .iban("FR7610011000201234567890188") // IBAN
                .bic("PSSTFRPP")                     // Optional BIC
                .build();

        // Transactions
        Transaction transaction1 = builders.transaction()
                .party(builders.party()              // Optional creditor identification
                        .name("BANQUE DE FRANCE")               // Optional name
                        .postalAddress(builders.postalAddress() // Optional postal address
                                .addressLine("1, rue de La Vrillière")
                                .addressLine("75001 PARIS")
                                .country("FR")
                                .build())
                        .build())
                .account(builders.bankAccount()     // Creditor account
                        .iban("FR7630001007941234567890185") // IBAN
                        .bic("BDFEFRPP")                     // Optional BIC
                        .build())
                .amount("12.34")                    // Amount, converted to BigDecimal
                .currency("EUR")                    // Currency code
                .endToEndId("Transfer reference 1") // End to end identifier
                .id("Optional identifier 1")        // Optional Transaction identifier
                .build();
        Transaction transaction2 = builders.transaction()
                .party(builders.party()                    // Optional creditor identification
                        .name("Creditor Name")             // Optional name
                        .build())
                .account(builders.bankAccount()     // Creditor account
                        .otherId("1234567890")               // Other identification
                        .bic("BDFEFRPP")                     // BIC
                        .build())
                .amount(new BigDecimal("56.78"))    // Amount as BigDecimal
                .currency("EUR")                    // Currency code
                .endToEndId("Transfer reference 2") // End to end identifier
                .id("Optional identifier 2")        // Optional transaction identifier
                .intermediaryAgent(builders.bankAccount() // Optional intermediary agent 1
                        .name("BNP PARIBAS")                 // Optional name
                        .otherId("12345")                    // Optional other identification
                        .bic("BNPAFRPP")                     // Optional BIC
                        .build())
                .intermediaryAgent(builders.bankAccount() // Optional intermediary agent 2
                        .name("BNP PARIBAS")                 // Optional name
                        .otherId("67890")                    // Optional other identification
                        .bic("BNPAFRPP")                     // Optional BIC
                        .build())
                .intermediaryAgent(builders.bankAccount() // Optional intermediary agent 3
                        .name("BNP PARIBAS")                 // Optional name
                        .otherId("00000")                    // Optional other identification
                        .bic("BNPAFRPP")                     // Optional BIC
                        .build())
                .build();

        // Transfer
        JAXB2CreditTransfer jaxb2CreditTransfer = new JAXB2CreditTransfer(builders.creditTransferSepa()
                .debtor(debtor)                                      // Optional debtor
                .debtorAccount(debtorAccount)                        // Mandatory debtor account
                .transaction(transaction1)                           // At least 1 transaction
                .transaction(transaction2)                           // Optional additional transaction
                .creationDateTime(LocalDateTime.now())               // Optional message creation date and time, defaults to now
                .requestedExecutionDate(LocalDate.now().plusDays(1)) // Optional requested execution date, defaults to tomorrow
                .id("MYID")                                          // Optional identifier, defaults to creation date and time as yyyyMMddhhmmss
                .build());

        // export to string
        String formattedOutput = jaxb2CreditTransfer.marshal(true); // true: enables formatting

        // or export to file
        jaxb2CreditTransfer.marshal(new FileWriter("myFile.xml")); // default: disables formatting
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
            <CreDtTm>2023-10-31T11:09:49.921</CreDtTm>
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
            <ReqdExctnDt>2023-11-01</ReqdExctnDt>
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
                <IntrmyAgt1>
                    <FinInstnId>
                        <BIC>BNPAFRPP</BIC>
                    </FinInstnId>
                </IntrmyAgt1>
                <IntrmyAgt1Acct>
                    <Id>
                        <Othr>
                            <Id>12345</Id>
                        </Othr>
                    </Id>
                    <Nm>BNP PARIBAS</Nm>
                </IntrmyAgt1Acct>
                <IntrmyAgt2>
                    <FinInstnId>
                        <BIC>BNPAFRPP</BIC>
                    </FinInstnId>
                </IntrmyAgt2>
                <IntrmyAgt2Acct>
                    <Id>
                        <Othr>
                            <Id>67890</Id>
                        </Othr>
                    </Id>
                    <Nm>BNP PARIBAS</Nm>
                </IntrmyAgt2Acct>
                <IntrmyAgt3>
                    <FinInstnId>
                        <BIC>BNPAFRPP</BIC>
                    </FinInstnId>
                </IntrmyAgt3>
                <IntrmyAgt3Acct>
                    <Id>
                        <Othr>
                            <Id>00000</Id>
                        </Othr>
                    </Id>
                    <Nm>BNP PARIBAS</Nm>
                </IntrmyAgt3Acct>
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
