package io.inisos.bank4j;

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

    public static void validateCreditTransfer(CustomerCreditTransferInitiationVersion version, Reader reader) throws IOException, JAXBException, SAXException {

        String xsd;
        Class<?> clazz;
        switch (version) {
            case V03:
                xsd = "pain.001.001.03.xsd";
                clazz = iso._20022.pain_001_001_03.ObjectFactory.class;
                break;
            case V09:
                xsd = "pain.001.001.09.xsd";
                clazz = iso._20022.pain_001_001_09.ObjectFactory.class;
                break;
            case V12:
                xsd = "pain.001.001.12.xsd";
                clazz = iso._20022.pain_001_001_12.ObjectFactory.class;
                break;
            case V003_03:
                xsd = "pain.001.003.03.xsd";
                clazz = iso._20022.pain_001_003_03.ObjectFactory.class;
                break;
            default:
                throw new IllegalArgumentException("Unsupported version: " + version);
        }

        try (InputStream resourceAsStream = SchemaValidator.class.getClassLoader().getResourceAsStream(xsd)) {

            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            Source xsdFile = new StreamSource(resourceAsStream);

            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(xsdFile);
            jaxbUnmarshaller.setSchema(schema);

            jaxbUnmarshaller.unmarshal(reader);
        }
    }
}
