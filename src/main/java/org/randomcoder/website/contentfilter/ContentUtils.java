package org.randomcoder.website.contentfilter;

import org.randomcoder.website.data.ContentType;
import org.randomcoder.website.io.SequenceReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.transform.Result;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ContentUtils {

    public static void format(String mimeType, URL baseUrl, InputSource content, ContentFilter filter, Result output)
            throws TransformerException, IOException, SAXException {
        TransformerFactory tFactory = TransformerFactory.newInstance();

        SAXTransformerFactory stFactory = (SAXTransformerFactory) tFactory;

        Templates templates = filter.getXSLTemplates(mimeType);

        TransformerHandler tHandler = null;
        if (templates == null)
            tHandler = stFactory.newTransformerHandler();
        else
            tHandler = stFactory.newTransformerHandler(templates);

        tHandler.setResult(output);

        XMLReader reader = filter.getXMLReader(baseUrl, mimeType);
        reader.setContentHandler(tHandler);
        reader.parse(content);
    }

    public static String format(String mimeType, URL baseUrl, InputSource content, ContentFilter filter)
            throws TransformerException, IOException, SAXException {
        StringWriter out = new StringWriter();
        format(mimeType, baseUrl, content, filter, new StreamResult(out));
        return out.toString();
    }

    public static String formatText(String content, URL baseUrl, ContentType contentType, ContentFilter filter)
            throws TransformerException, IOException, SAXException {
        String mimeType = contentType.getMimeType();

        String prefix = filter.getPrefix(mimeType);
        String suffix = filter.getSuffix(mimeType);

        List<Reader> readers = new ArrayList<Reader>();
        if (prefix != null)
            readers.add(new StringReader(prefix));

        readers.add(new StringReader(content));

        if (suffix != null)
            readers.add(new StringReader(suffix));

        SequenceReader reader = new SequenceReader(readers);

        return format(mimeType, baseUrl, new InputSource(reader), filter);
    }

    public static void formatText(String content, URL baseUrl, ContentType contentType, ContentFilter filter, Result result)
            throws TransformerException, IOException, SAXException {
        String mimeType = contentType.getMimeType();

        String prefix = filter.getPrefix(mimeType);
        String suffix = filter.getSuffix(mimeType);

        List<Reader> readers = new ArrayList<Reader>();
        if (prefix != null)
            readers.add(new StringReader(prefix));

        readers.add(new StringReader(content));

        if (suffix != null)
            readers.add(new StringReader(suffix));

        SequenceReader reader = new SequenceReader(readers);

        format(mimeType, baseUrl, new InputSource(reader), filter, result);
    }

}
