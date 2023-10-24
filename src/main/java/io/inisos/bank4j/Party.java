package io.inisos.bank4j;

import java.util.Optional;

/**
 * @author Patrice Blanchardie
 */
public interface Party {
    Optional<String> getName();

    Optional<PostalAddress> getPostalAddress();
}
