package io.inisos.bank4j.impl;

import io.inisos.bank4j.Party;
import io.inisos.bank4j.PostalAddress;

import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

public class SimpleParty implements Party {
    private final String name;
    private final PostalAddress postalAddress;

    public SimpleParty(String name, PostalAddress postalAddress) {
        this.name = name;
        this.postalAddress = postalAddress;
    }

    @Override
    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    @Override
    public Optional<PostalAddress> getPostalAddress() {
        return Optional.ofNullable(postalAddress);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleParty)) return false;
        SimpleParty that = (SimpleParty) o;
        return Objects.equals(name, that.name) && Objects.equals(postalAddress, that.postalAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, postalAddress);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SimpleParty.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("postalAddress=" + postalAddress)
                .toString();
    }
}
