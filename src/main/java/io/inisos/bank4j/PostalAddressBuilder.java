package io.inisos.bank4j;

import java.util.Collection;

public interface PostalAddressBuilder {
    PostalAddressBuilder type(String type);

    PostalAddressBuilder department(String department);

    PostalAddressBuilder subDepartment(String subDepartment);

    PostalAddressBuilder streetName(String streetName);

    PostalAddressBuilder buildingNumber(String buildingNumber);

    PostalAddressBuilder postCode(String postCode);

    PostalAddressBuilder townName(String townName);

    PostalAddressBuilder countrySubDivision(String countrySubDivision);

    PostalAddressBuilder country(String country);

    PostalAddressBuilder addressLines(Collection<String> addressLines);

    PostalAddressBuilder addressLine(String addressLine);

    PostalAddress build();
}
