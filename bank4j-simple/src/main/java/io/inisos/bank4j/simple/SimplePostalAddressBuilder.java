package io.inisos.bank4j.simple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Patrice Blanchardie
 */
public class SimplePostalAddressBuilder implements io.inisos.bank4j.PostalAddressBuilder {
    private String type;
    private String department;
    private String subDepartment;
    private String streetName;
    private String buildingNumber;
    private String postCode;
    private String townName;
    private String countrySubDivision;
    private String country;
    private final List<String> addressLines = new ArrayList<>();

    @Override
    public SimplePostalAddressBuilder type(String type) {
        this.type = type;
        return this;
    }

    @Override
    public SimplePostalAddressBuilder department(String department) {
        this.department = department;
        return this;
    }

    @Override
    public SimplePostalAddressBuilder subDepartment(String subDepartment) {
        this.subDepartment = subDepartment;
        return this;
    }

    @Override
    public SimplePostalAddressBuilder streetName(String streetName) {
        this.streetName = streetName;
        return this;
    }

    @Override
    public SimplePostalAddressBuilder buildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
        return this;
    }

    @Override
    public SimplePostalAddressBuilder postCode(String postCode) {
        this.postCode = postCode;
        return this;
    }

    @Override
    public SimplePostalAddressBuilder townName(String townName) {
        this.townName = townName;
        return this;
    }

    @Override
    public SimplePostalAddressBuilder countrySubDivision(String countrySubDivision) {
        this.countrySubDivision = countrySubDivision;
        return this;
    }

    @Override
    public SimplePostalAddressBuilder country(String country) {
        this.country = country;
        return this;
    }

    @Override
    public SimplePostalAddressBuilder addressLines(Collection<String> addressLines) {
        this.addressLines.addAll(addressLines);
        return this;
    }

    @Override
    public SimplePostalAddressBuilder addressLine(String addressLine) {
        this.addressLines.add(addressLine);
        return this;
    }

    @Override
    public SimplePostalAddress build() {
        return new SimplePostalAddress(type, department, subDepartment, streetName, buildingNumber, postCode, townName, countrySubDivision, country, addressLines.toArray(new String[0]));
    }
}
