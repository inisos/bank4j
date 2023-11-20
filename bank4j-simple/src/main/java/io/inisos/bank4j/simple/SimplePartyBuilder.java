package io.inisos.bank4j.simple;

import io.inisos.bank4j.PostalAddress;

/**
 * @author Patrice Blanchardie
 */
public class SimplePartyBuilder implements io.inisos.bank4j.PartyBuilder {
    private String name;
    private PostalAddress postalAddress;

    @Override
    public SimplePartyBuilder name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public SimplePartyBuilder postalAddress(PostalAddress postalAddress) {
        this.postalAddress = postalAddress;
        return this;
    }

    @Override
    public SimpleParty build() {
        return new SimpleParty(name, postalAddress);
    }
}
