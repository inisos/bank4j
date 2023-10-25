package io.inisos.bank4j;

/**
 * @author Patrice Blanchardie
 */
public class Bank4jException extends RuntimeException {
    public Bank4jException(String message) {
        super(message);
    }

    public Bank4jException(Throwable cause) {
        super(cause);
    }
}
