package io.inisos.bank4j;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MyApp {
    public static void main(String... args) throws IOException {

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
        System.out.println(formattedOutput);

        // or export to file
        creditTransfer.marshal(new FileWriter("myFile.xml")); // default: disables formatting
    }
}
