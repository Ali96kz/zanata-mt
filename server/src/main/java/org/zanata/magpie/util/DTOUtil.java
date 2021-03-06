package org.zanata.magpie.util;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

@ApplicationScoped
public class DTOUtil {
    private final static Logger LOG = LoggerFactory.getLogger(DTOUtil.class);

    @SuppressWarnings({ "unchecked" })
    public <T> String toXML(T obj) {
        try {
            JAXBContext jc = JAXBContext.newInstance(obj.getClass());
            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.setProperty(Marshaller.JAXB_FRAGMENT, true);
            StringWriter writer = new StringWriter();

            /*
             * Marshal objects into a string differently depending on whether
             * they are root elements or not
             */
            if (obj.getClass().getAnnotation(XmlRootElement.class) != null) {
                m.marshal(obj, writer);
            } else {
                m.marshal(new JAXBElement<T>(new QName("", obj.getClass()
                        .getSimpleName()), (Class<T>) obj.getClass(), obj),
                        writer);
            }
            return writer.toString();
        } catch (Exception e) {
            LOG.error("toXML failed", e);
            return obj.getClass().getName() + "@"
                    + Integer.toHexString(obj.hashCode());
        }
    }

    public <T> T toObject(String xml, Class<T> clazz)
            throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(clazz);
        Unmarshaller um = jc.createUnmarshaller();
        StringReader reader = new StringReader(xml);
        StreamSource source = new StreamSource(reader);
        JAXBElement<T> elem = um.unmarshal(source, clazz);
        return elem.getValue();
    }

    public String toJSON(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(obj);
        } catch (IOException e) {
            LOG.error("toJSON failed", e);
            return obj.getClass().getName() + "@"
                    + Integer.toHexString(obj.hashCode());
        }
    }

    public static <T> T fromJSONToObject(String json, Class<T> clazz)
        throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, clazz);
    }
}
