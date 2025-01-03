## bank4j

Easily generate XML Credit Transfers based on [ISO 20022](https://www.iso20022.org/) Payments Initiation `pain.001.001.03`.

Provides IBAN and BIC validation with annotations.

Using JAXB and [iban4j](https://github.com/arturmkrtchyan/iban4j).

### Installation

```xml

<dependency>
    <groupId>io.inisos.bank4j</groupId>
    <artifactId>bank4j</artifactId>
    <version>3.0.1</version>
</dependency>
```

* Use version 3 for `jarkarta.*` dependencies (Java 11, JAXB 3+, Bean Validation 3+)
* Use version 2 for `javax.*` dependencies (Java 8, JAXB 2, Bean Validation 2)

## Usage

### Validation

```java
class MyRecord {
    
    @IBAN
    private String iban;

    @BIC
    private String bic;
    
    @Iso20022CharacterSet
    private String reference;
    
}
```

Only accepts valid IBAN, BIC8/BIC11 and valid ISO 20022 patterns.

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
                .amount("12.34")                                // Amount, converted to BigDecimal
                .currency("EUR")                                // Currency code
                .endToEndId("Transfer reference 1")             // End to end identifier
                .id("Optional identifier 1")                    // Optional Transaction identifier
                .chargeBearerCode(ChargeBearerType1Code.CRED)   // Optional charge bearer code defines who is bearing the charges of the transfer 
                .remittanceInformationUnstructured("Your remittance information")   // Unstructured Remittance Information 
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
                .chargeBearerCode(ChargeBearerType1Code.DEBT)   // Optional charge bearer code defines who is bearing the charges of the transfer
                .intermediaryAgent(Bank.simpleBankAccount() // Optional intermediary agent 1
                        .name("BNP PARIBAS")                 // Optional name
                        .otherId("12345")                    // Optional other identification
                        .bic("BNPAFRPP")                     // Optional BIC
                        .build())
                .intermediaryAgent(Bank.simpleBankAccount() // Optional intermediary agent 2
                        .name("BNP PARIBAS")                 // Optional name
                        .otherId("67890")                    // Optional other identification
                        .bic("BNPAFRPP")                     // Optional BIC
                        .build())
                .intermediaryAgent(Bank.simpleBankAccount() // Optional intermediary agent 3
                        .name("BNP PARIBAS")                 // Optional name
                        .otherId("00000")                    // Optional other identification
                        .bic("BNPAFRPP")                     // Optional BIC
                        .build())
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
                .chargeBearerCode(ChargeBearerType1Code.DEBT)        // Optional charge bearer code defines who is bearing the charges of the transfer 
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
                <RmtInf>
                    <Ustrd>Your remittance information</Ustrd>
                </RmtInf>
            </CdtTrfTxInf>
        </PmtInf>
    </CstmrCdtTrfInitn>
</Document>
```

## Go further

### Using the `Iso20022ReferenceElementValidator` class
The `Iso20022ReferenceElementValidator` class provides methods for validating and processing reference elements according to ISO 20022 standards.

By default, these reference fields are validated with an annotation.

#### Sanitization of reference elements
If you can't anticipate the value for these reference fields and the validation doesn't pass, you can use this class we provide to automatically sanitize your fields using the `sanitizeToCharacterSet` method :

```java
String input = "/endTo#EndId";
Map<Character, Character> customReplacements = new HashMap<>();
customReplacements.put('#', '-'); // will replace all "#" chars with "-"

Iso20022ReferenceElementValidator.sanitizeToCharacterSet(input, customReplacements);
// Display "endTo-EndId"
```
_Note: the mapping for replacements (`customRemplacements`) is optional, we offer a default replacement (`.`) by default._

#### Input validation
The `isValidCharacterSet` method checks whether a string respects the valid characters defined by the ISO 20022 standard. It returns a boolean. Here's an example:

```java
Iso20022ReferenceElementValidator.isValidCharacterSet("ABCDEF1234"); // true
```

### Interfaces

You can use your own implementations of `Transaction`, `BankAccount`, `Party` and `PostalAddress` but simple defaults are provided.
