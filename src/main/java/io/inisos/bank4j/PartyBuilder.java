package io.inisos.bank4j;

/**
 * @author Patrice Blanchardie
 */
public interface PartyBuilder {
    PartyBuilder name(String name);

    PartyBuilder postalAddress(PostalAddress postalAddress);

    Party build();
}
