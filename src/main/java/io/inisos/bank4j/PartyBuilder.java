package io.inisos.bank4j;

public interface PartyBuilder {
    PartyBuilder name(String name);

    PartyBuilder postalAddress(PostalAddress postalAddress);

    Party build();
}
