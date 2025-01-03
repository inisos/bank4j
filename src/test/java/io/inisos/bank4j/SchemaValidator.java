package io.inisos.bank4j;

import iso.std.iso._20022.tech.xsd.pain_001_001.ObjectFactory;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class SchemaValidator {

    public static void validateCreditTransfer(Reader reader) throws IOException, JAXBException, SAXException {
        try (InputStream resourceAsStream = SchemaValidator.class.getClassLoader().getResourceAsStream("pain.001.001.03.xsd")) {

            JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            Source xsdFile = new StreamSource(resourceAsStream);

            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(xsdFile);
            jaxbUnmarshaller.setSchema(schema);

            jaxbUnmarshaller.unmarshal(reader);
        }
    }
}
