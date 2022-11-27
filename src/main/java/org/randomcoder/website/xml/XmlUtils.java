package org.randomcoder.website.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringWriter;

public final class XmlUtils {

    private static final Logger logger = LoggerFactory.getLogger(XmlUtils.class);

    private XmlUtils() {
    }

    public static Document parseXml(InputSource source) throws SAXException, IOException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);

            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(source);
        } catch (ParserConfigurationException e) {
            logger.error("Caught exception", e);
            throw new RuntimeException("XML parser configuration problem", e);
        }
    }

    public static void writeXml(Document doc, Result result, String publicId, String systemId) throws TransformerException {
        Transformer trans = getTransformer(true, publicId, systemId, null);
        trans.transform(new DOMSource(doc), result);
    }

    public static Transformer getTransformer(boolean indent, String publicId, String systemId, Source xsl) {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();

            // try to set indent amount using JDK 1.5+ property
            try {
                if (indent) {
                    factory.setAttribute("indent-number", Integer.valueOf(2));
                }
            } catch (Exception ignored) {
            }

            Transformer trans = (xsl == null) ? factory.newTransformer() : factory.newTransformer(xsl);

            trans.setOutputProperty(OutputKeys.METHOD, "xml");

            if (publicId != null) {
                trans.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, publicId);
            }
            if (systemId != null) {
                trans.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, systemId);
            }

            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");

            // try to set indent amount using Xalan property
            try {
                if (indent) {
                    trans.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");
                }
            } catch (Exception ignored) {
            }

            return trans;
        } catch (TransformerConfigurationException e) {
            // make this unchecked since there's nothing other than environment
            // that
            // should cause this
            logger.error("Caught exception", e);
            throw new RuntimeException("Transformer configuration problem", e);
        }
    }

    public static void logXml(Logger log, Document doc) {
        logXml(log, null, doc, null, null);
    }

    public static void logXml(Logger log, Document doc, String publicId, String systemId) {
        logXml(log, null, doc, publicId, systemId);
    }

    public static void logXml(Logger log, String message, Document doc) {
        logXml(log, message, doc, null, null);
    }

    public static void logXml(Logger log, String message, Document doc, String publicId, String systemId) {
        if (!log.isDebugEnabled()) {
            return;
        }

        try {
            StringWriter writer = new StringWriter();
            if (message != null) {
                writer.write(message);
            }
            writer.write("\n");
            writeXml(doc, new StreamResult(writer), publicId, systemId);
            writer.close();
            log.debug(writer.toString());
        } catch (Exception ignored) {
        }
    }

    public static void prettyPrint(Document input, StreamResult output) throws TransformerException {
        prettyPrint(input, output, null);
    }

    public static void prettyPrint(Document input, StreamResult output, DocumentType docType) throws TransformerException {
        String publicId = null;
        String systemId = null;
        if (docType != null) {
            publicId = docType.getPublicId();
            systemId = docType.getSystemId();
        }

        StreamSource xsl = new StreamSource(XmlUtils.class.getResourceAsStream("pretty-print.xsl"));
        Transformer t = getTransformer(true, publicId, systemId, xsl);

        DOMSource src = new DOMSource(input);
        t.transform(src, output);
    }

}
