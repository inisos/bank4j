package io.inisos.bank4j.simple;


import io.inisos.bank4j.PostalAddress;

import java.util.*;

/**
 * @author Patrice Blanchardie
 */
public class SimplePostalAddress implements PostalAddress {
    private final String type;
    private final String department;
    private final String subDepartment;
    private final String streetName;
    private final String buildingNumber;
    private final String postCode;
    private final String townName;
    private final String countrySubDivision;
    private final String country;
    private final List<String> addressLines = new ArrayList<>();

    @SuppressWarnings("java:S107")
    public SimplePostalAddress(String type, String department, String subDepartment, String streetName, String buildingNumber, String postCode, String townName, String countrySubDivision, String country, String... addressLines) {
        this.type = requireMaxSize(type, 4);
        this.department = requireMaxSize(department, 70);
        this.subDepartment = requireMaxSize(subDepartment, 70);
        this.streetName = requireMaxSize(streetName, 70);
        this.buildingNumber = requireMaxSize(buildingNumber, 16);
        this.postCode = requireMaxSize(postCode, 16);
        this.townName = requireMaxSize(townName, 35);
        this.countrySubDivision = requireMaxSize(countrySubDivision, 35);
        this.country = requireMaxSize(country, 2);
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

    private static String requireMaxSize(String value, int maxSize) {
        if (value != null && value.length() > maxSize) {
            throw new IllegalArgumentException("value '" + value + "' exceeds max size " + maxSize);
        }
        return value;
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
