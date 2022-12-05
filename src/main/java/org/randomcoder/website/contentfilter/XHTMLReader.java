package org.randomcoder.website.contentfilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class XHTMLReader extends XMLFilterImpl {

    private static final Logger logger = LoggerFactory.getLogger(XHTMLReader.class);

    private static final Attributes NO_ATTRIBUTES = new AttributesImpl();
    private static final String HTML = "html".toLowerCase(Locale.US);

    private static final Set<String> ALLOWED_TAGS;
    private static final Map<String, String> REPLACED_TAGS;
    private static final Set<String> ALLOWED_ATTRIBUTES;
    private static final Set<String> URL_ATTRIBUTES;
    private static final Set<String> ALLOWED_PROTOCOLS;

    static {
        String[] tags = {
                "a", "abbr", "acronym", "address", "bdo", "big", "blockquote", "body",
                "br", "caption", "cite", "code", "colgroup", "dd", "del", "dfn",
                "div", "dl", "dt", "em", "h1", "h2", "h3", "h4", "h5", "h6", "hr",
                "html", "img", "ins", "kbd", "li", "ol", "p", "pre", "q", "samp",
                "small", "span", "strong", "sub", "sup", "table", "tbody", "td",
                "tfoot", "th", "thead", "tr", "tt", "ul", "var"};

        Set<String> set = new HashSet<String>(tags.length);
        for (String tag : tags) {
            set.add(tag.toLowerCase(Locale.US));
        }
        ALLOWED_TAGS = Collections.unmodifiableSet(set);
    }

    static {
        String[][] tags = {{"b", "strong"}, {"i", "em"}, {"s", "del"}, {"strike", "del"}, {"u", "em"}};
        HashMap<String, String> map = new HashMap<String, String>(tags.length);
        for (String[] entry : tags)
            map.put(entry[0].toLowerCase(Locale.US), entry[1].toLowerCase(Locale.US));
        REPLACED_TAGS = Collections.unmodifiableMap(map);
    }

    static {
        String[] atts = {
                "*.dir", "*.lang", "*.title", "a.href", "a.charset", "a.hreflang",
                "a.type", "blockquote.cite", "body.-", "colgroup.align",
                "colgroup.char", "colgroup.charoff", "colgroup.span",
                "colgroup.valign", "colgroup.width", "del.cite", "del.datetime",
                "html.-", "img.alt", "img.src", "img.height", "img.longdesc",
                "img.width", "ins.cite", "ins.datetime", "pre.width", "q.cite",
                "table.border", "table.cellpadding", "table.cellspacing",
                "table.frame", "table.rules", "table.summary", "table.width",
                "tbody.align", "tbody.char", "tbody.charoff", "tbody.valign",
                "td.abbr", "td.align", "td.axis", "td.char", "td.charoff",
                "td.colspan", "td.headers", "td.rowspan", "td.scope", "td.valign",
                "tfoot.align", "tfoot.char", "tfoot.charoff", "tfoot.valign",
                "th.abbr", "th.align", "th.axis", "th.char", "th.charoff",
                "th.colspan", "th.headers", "th.rowspan", "th.scope", "th.valign",
                "thead.align", "thead.char", "thead.charoff", "thead.valign",
                "tr.align", "tr.char", "tr.charoff", "tr.valign"};
        Set<String> set = new HashSet<String>(atts.length);
        for (String att : atts) {
            set.add(att.toLowerCase(Locale.US));
        }
        ALLOWED_ATTRIBUTES = Collections.unmodifiableSet(set);
    }

    static {
        String[] atts = {
                "*.href", "*.src", "*.cite", "*.xmlns", "body.background",
                "form.action", "frame.longdesc", "head.profile", "img.ismap",
                "img.longdesc", "img.usemap", "object.archive", "object.codebase",
                "object.data", "object.usemap"};

        Set<String> set = new HashSet<String>(atts.length);
        for (String att : atts) {
            set.add(att.toLowerCase(Locale.US));
        }
        URL_ATTRIBUTES = Collections.unmodifiableSet(set);
    }

    static {
        String[] protos = {"http", "https", "ftp", "mailto"};
        Set<String> set = new HashSet<String>(protos.length);
        for (String proto : protos) {
            set.add(proto.toLowerCase(Locale.US));
        }
        ALLOWED_PROTOCOLS = Collections.unmodifiableSet(set);
    }

    private final Set<String> allowedClasses;
    private final URL baseUrl;
    private int elementLevel = -1;
    private int filterLevel = -1;

    public XHTMLReader(XMLReader parent, Set<String> allowedClasses, URL baseUrl) {
        super(parent);
        this.allowedClasses = allowedClasses;
        this.baseUrl = baseUrl;
    }

    private boolean isAllowedElement(String elName) {
        return ALLOWED_TAGS.contains(elName.toLowerCase(Locale.US));
    }

    private boolean isAllowedAttribute(String elName, String attName) {
        if (ALLOWED_ATTRIBUTES.contains(elName + ".-")) {
            return false;
        }
        if (ALLOWED_ATTRIBUTES.contains("*." + attName)) {
            return true;
        }

        return ALLOWED_ATTRIBUTES.contains(elName + "." + attName);
    }

    private boolean isUrlAttribute(String elName, String attName) {
        if (URL_ATTRIBUTES.contains("*." + attName)) {
            return true;
        }

        return URL_ATTRIBUTES.contains(elName + "." + attName);
    }

    private boolean validateUrl(String url) {
        try {
            URL context = new URL("http://localhost/");
            URL testUrl = new URL(context, url);

            String proto = testUrl.getProtocol();
            if (proto == null) {
                return true;
            }
            proto = proto.toLowerCase(Locale.US);

            if (ALLOWED_PROTOCOLS.contains(proto)) {
                return true;
            }

            logger.warn("Invalid protocol: " + proto);

            return false;
        } catch (MalformedURLException e) {
            logger.warn("Malformed URL: + " + url + " (" + e.getMessage() + ")");
        }

        return false;
    }

    private String rebaseUrl(String url) {
        try {
            return new URL(baseUrl, url).toExternalForm();
        } catch (MalformedURLException e) {
            // shouldn't happen...
            return "#";
        }
    }

    private String getCanonicalElement(String localName) {
        String canon = REPLACED_TAGS.get(localName);
        if (canon != null) {
            return canon;
        }
        return localName.toLowerCase(Locale.US);
    }

    private String getCanonicalAttribute(String localName) {
        return localName.toLowerCase(Locale.US);
    }

    private void addClassAttribute(AttributesImpl attributes, String value) {
        String[] classes = value.split("\\s+");

        StringBuilder buf = new StringBuilder();
        for (String cl : classes) {
            cl = cl.trim();
            if (allowedClasses.contains(cl)) {
                if (buf.length() > 0) {
                    buf.append(" ");
                }
                buf.append(cl);
            }
        }
        if (buf.length() > 0) {
            attributes.addAttribute("", "class", "class", "CDATA", buf.toString());
        }
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        elementLevel = -1;
        filterLevel = -1;
    }

    @Override
    public void endDocument() throws SAXException {
        if (elementLevel == 1) {
            // match the content we added
            super.endElement("", "body", "body");
            super.endElement("", "html", "html");
        }
        super.endDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        String tagName = getCanonicalElement(localName);

        elementLevel++;

        if (elementLevel == 0) {
            // if 'html' is not the root element, substitute our own
            if (!HTML.equals(tagName)) {
                // substitute our own wrapper
                super.startElement("", "html", "html", NO_ATTRIBUTES);
                super.startElement("", "body", "body", NO_ATTRIBUTES);
                elementLevel += 2;
            }
        }

        // if filtering, skip this
        if (filterLevel >= 0) {
            return;
        }

        // check to see if element is allowed
        if (!isAllowedElement(tagName)) {
            filterLevel = elementLevel;
            return;
        }

        // filter attributes
        AttributesImpl filteredAtts = new AttributesImpl();
        for (int i = 0; i < atts.getLength(); i++) {
            String attName = getCanonicalAttribute(atts.getLocalName(i));

            if ("class".equals(attName)) {
                addClassAttribute(filteredAtts, atts.getValue(i));
            } else if (isAllowedAttribute(tagName, attName)) {
                if (isUrlAttribute(tagName, attName)) {
                    // filter urls
                    String url = atts.getValue(i);

                    if (validateUrl(url)) {
                        if (baseUrl != null) {
                            url = rebaseUrl(url);
                        }
                        filteredAtts.addAttribute("", attName, attName, atts.getType(i), url);
                    } else {
                        // not valid -- replace with safe value
                        filteredAtts.addAttribute("", attName, attName, atts.getType(i), "#");
                    }
                } else {
                    filteredAtts.addAttribute("", attName, attName, atts.getType(i), atts.getValue(i));
                }
            }
        }

        super.startElement("", tagName, tagName, filteredAtts);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            String tagName = getCanonicalElement(localName);

            // check for filtered elements
            if (filterLevel >= 0) {
                if (filterLevel == elementLevel) {
                    filterLevel = -1;
                }
                return;
            }

            super.endElement("", tagName, tagName);
        } finally {
            elementLevel--;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (filterLevel >= 0) {
            return;
        }
        super.characters(ch, start, length);
    }

    /**
     * Processes ignorable whitespace.
     *
     * @param ch     character buffer to read
     * @param start  starting offset in buffer
     * @param length number of characters to read
     * @throws SAXException if an error occurs
     */
    @Override
    public void ignorableWhitespace(char[] ch, int start, int length)
            throws SAXException {
        if (filterLevel >= 0)
            return;
        super.ignorableWhitespace(ch, start, length);
    }

    /**
     * Handles processing instructions.
     *
     * @param target target of processing instruction
     * @param data   associated data
     * @throws SAXException if an error occurs
     */
    @Override
    public void processingInstruction(String target, String data)
            throws SAXException {
        if (filterLevel >= 0)
            return;
        super.processingInstruction(target, data);
    }

    @Override
    public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
        if (filterLevel >= 0) {
            return;
        }
        super.unparsedEntityDecl(name, publicId, systemId, notationName);
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        return super.resolveEntity(publicId, systemId);
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        if (filterLevel >= 0) {
            return;
        }
        super.skippedEntity(name);
    }

    @Override
    public void notationDecl(String name, String publicId, String systemId) throws SAXException {
        if (filterLevel >= 0) {
            return;
        }
        super.notationDecl(name, publicId, systemId);
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        if ("".equals(prefix)) {
            return; // don't map default prefix
        }
        super.startPrefixMapping(prefix, uri);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        if ("".equals(prefix)) {
            return; // don't map default prefix
        }
        super.endPrefixMapping(prefix);
    }

}
