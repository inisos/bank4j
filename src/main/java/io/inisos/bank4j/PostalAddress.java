package io.inisos.bank4j;

import java.util.List;
import java.util.Optional;

/**
 * @author Patrice Blanchardie
 */
public interface PostalAddress {
    Optional<String> getType();

    Optional<String> getDepartment();

    Optional<String> getSubDepartment();

    Optional<String> getStreetName();

    Optional<String> getBuildingNumber();

    Optional<String> getPostCode();

    Optional<String> getTownName();

    Optional<String> getCountrySubDivision();

    Optional<String> getCountry();

    List<String> getAddressLines();
}
