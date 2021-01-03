package io.inisos.bank4j;

import java.io.StringWriter;
import java.io.Writer;

/**
 * An Operation that can be marshalled
 */
public interface Operation {

    /**
     * Marshall as String without formatting
     *
     * @return marshalled operation
     */
    default String marshal() {
        return marshal(false);
    }

    /**
     * Marshall as String
     *
     * @param formatted true to format output, false to leave unformatted
     * @return marshalled operation
     */
    default String marshal(boolean formatted) {
        StringWriter sw = new StringWriter();
        marshal(sw, formatted);
        return sw.toString();
    }

    /**
     * Marshall to writer without formatting
     *
     * @param writer writer
     */
    default void marshal(Writer writer) {
        marshal(writer, false);
    }

    /**
     * Marshall to writer
     *
     * @param writer    writer
     * @param formatted true to format output, false to leave unformatted
     */
    void marshal(Writer writer, boolean formatted);

}
