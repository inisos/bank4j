package io.inisos.bank4j;

import java.util.Optional;

public interface Party {
    Optional<String> getName();

    Optional<PostalAddress> getPostalAddress();
}
