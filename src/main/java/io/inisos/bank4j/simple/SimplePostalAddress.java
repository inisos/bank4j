package io.inisos.bank4j.simple;


import io.inisos.bank4j.PostalAddress;

import javax.validation.constraints.Size;
import java.util.*;

/**
 * @author Patrice Blanchardie
 */
public class SimplePostalAddress implements PostalAddress {
    @Size(max = 4)
    private final String type;
    @Size(max = 70)
    private final String department;
    @Size(max = 70)
    private final String subDepartment;
    @Size(max = 70)
    private final String streetName;
    @Size(max = 16)
    private final String buildingNumber;
    @Size(max = 16)
    private final String postCode;
    @Size(max = 35)
    private final String townName;
    @Size(max = 35)
    private final String countrySubDivision;
    @Size(max = 2)
    private final String country;
    private final List<String> addressLines = new ArrayList<>();

    @SuppressWarnings("java:S107")
    public SimplePostalAddress(String type, String department, String subDepartment, String streetName, String buildingNumber, String postCode, String townName, String countrySubDivision, String country, String... addressLines) {
        this.type = type;
        this.department = department;
        this.subDepartment = subDepartment;
        this.streetName = streetName;
        this.buildingNumber = buildingNumber;
        this.postCode = postCode;
        this.townName = townName;
        this.countrySubDivision = countrySubDivision;
        this.country = country;
        this.addressLines.addAll(Arrays.asList(addressLines));
    }

    @Override
    public Optional<String> getType() {
        return Optional.ofNullable(type);
    }

    @Override
    public Optional<String> getDepartment() {
        return Optional.ofNullable(department);
    }

    @Override
    public Optional<String> getSubDepartment() {
        return Optional.ofNullable(subDepartment);
    }

    @Override
    public Optional<String> getStreetName() {
        return Optional.ofNullable(streetName);
    }

    @Override
    public Optional<String> getBuildingNumber() {
        return Optional.ofNullable(buildingNumber);
    }

    @Override
    public Optional<String> getPostCode() {
        return Optional.ofNullable(postCode);
    }

    @Override
    public Optional<String> getTownName() {
        return Optional.ofNullable(townName);
    }

    @Override
    public Optional<String> getCountrySubDivision() {
        return Optional.ofNullable(countrySubDivision);
    }

    @Override
    public Optional<String> getCountry() {
        return Optional.ofNullable(country);
    }

    @Override
    public List<String> getAddressLines() {
        return addressLines;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimplePostalAddress)) return false;
        SimplePostalAddress that = (SimplePostalAddress) o;
        return Objects.equals(type, that.type) && Objects.equals(department, that.department) && Objects.equals(subDepartment, that.subDepartment) && Objects.equals(streetName, that.streetName) && Objects.equals(buildingNumber, that.buildingNumber) && Objects.equals(postCode, that.postCode) && Objects.equals(townName, that.townName) && Objects.equals(countrySubDivision, that.countrySubDivision) && Objects.equals(country, that.country) && Objects.equals(addressLines, that.addressLines);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, department, subDepartment, streetName, buildingNumber, postCode, townName, countrySubDivision, country, addressLines);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SimplePostalAddress.class.getSimpleName() + "[", "]")
                .add("type='" + type + "'")
                .add("department='" + department + "'")
                .add("subDepartment='" + subDepartment + "'")
                .add("streetName='" + streetName + "'")
                .add("buildingNumber='" + buildingNumber + "'")
                .add("postCode='" + postCode + "'")
                .add("townName='" + townName + "'")
                .add("countrySubDivision='" + countrySubDivision + "'")
                .add("country='" + country + "'")
                .add("addressLines=" + addressLines)
                .toString();
    }
}
