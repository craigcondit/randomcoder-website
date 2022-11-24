package org.randomcoder.feed;

import org.randomcoder.bo.AppInfoBusiness;
import org.randomcoder.content.ContentFilter;
import org.randomcoder.content.ContentType;
import org.randomcoder.content.ContentUtils;
import org.randomcoder.db.Article;
import org.randomcoder.db.Tag;
import org.randomcoder.xml.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Generator for RSS 1.0 feeds.
 */
public class Rss20FeedGenerator implements FeedGenerator {
    private final AppInfoBusiness appInfoBusiness;
    private final URL baseUrl;
    private final ContentFilter contentFilter;

    /**
     * Creates a new RSS 2.0 feed generator.
     *
     * @param baseUrl         base URL
     * @param contentFilter   content filter
     * @param appInfoBusiness application information
     * @throws MalformedURLException if URL is invalid
     */
    public Rss20FeedGenerator(String baseUrl, ContentFilter contentFilter, AppInfoBusiness appInfoBusiness)
            throws MalformedURLException {
        this.baseUrl = new URL(baseUrl);
        this.contentFilter = contentFilter;
        this.appInfoBusiness = appInfoBusiness;
    }

    @Override
    public String generateFeed(FeedInfo info) throws FeedException {
        // need to write out XML
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);

        DecimalFormat df = new DecimalFormat("####################");

        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new FeedConfigurationException(
                    "Unable to generate document builder", e);
        }

        Document doc = db.newDocument();
        Element root = doc.createElement("rss");
        root.setAttribute("version", "2.0");
        doc.appendChild(root);

        Element channelEl = doc.createElement("channel");

        Element titleEl = doc.createElement("title");
        titleEl.appendChild(doc.createTextNode(info.getTitle()));
        channelEl.appendChild(titleEl);

        Element linkEl = doc.createElement("link");
        linkEl.appendChild(doc.createTextNode(info.getAltUrl().toExternalForm()));
        channelEl.appendChild(linkEl);

        String subtitle = info.getSubtitle();
        if (subtitle != null) {
            Element descriptionEl = doc.createElement("description");
            descriptionEl.appendChild(doc.createTextNode(subtitle));
            channelEl.appendChild(descriptionEl);
        }

        Element docsEl = doc.createElement("docs");
        docsEl.appendChild(
                doc.createTextNode("http://blogs.law.harvard.edu/tech/rss"));
        channelEl.appendChild(docsEl);

        Element generatorEl = doc.createElement("generator");
        generatorEl.appendChild(doc.createTextNode(
                appInfoBusiness.getApplicationName() + " " + appInfoBusiness
                        .getApplicationVersion()));
        channelEl.appendChild(generatorEl);

        Element languageEl = doc.createElement("language");
        languageEl.appendChild(doc.createTextNode("en-us"));
        channelEl.appendChild(languageEl);

        Element pubDateEl = doc.createElement("pubDate");
        channelEl.appendChild(pubDateEl);

        Date feedUpdated = null;

        for (Article article : info.getArticles()) {
            Element itemEl = doc.createElement("item");

            // write title
            Element articleTitleEl = doc.createElement("title");
            articleTitleEl.appendChild(doc.createTextNode(article.getTitle()));
            itemEl.appendChild(articleTitleEl);

            URL articleUrl = null;
            try {
                articleUrl = new URL(baseUrl, article.getPermalinkUrl());
            } catch (MalformedURLException e) {
                throw new FeedException("Unable to generate feed URL", e);
            }

            // write article link
            Element articleLinkEl = doc.createElement("link");
            articleLinkEl
                    .appendChild(doc.createTextNode(articleUrl.toExternalForm()));
            itemEl.appendChild(articleLinkEl);

            // write guid
            Element articleGuidEl = doc.createElement("guid");
            if (article.getPermalink() != null) {
                articleGuidEl.setAttribute("isPermaLink", "true");
            }

            articleGuidEl
                    .appendChild(doc.createTextNode(articleUrl.toExternalForm()));
            itemEl.appendChild(articleGuidEl);

            // write published
            Date published = article.getCreationDate();
            Element articlePubDateEl = doc.createElement("pubDate");
            articlePubDateEl.appendChild(doc.createTextNode(formatDate(published)));
            itemEl.appendChild(articlePubDateEl);

            if (feedUpdated == null || feedUpdated.before(published)) {
                feedUpdated = published;
            }

            // write categories
            for (Tag tag : article.getTags()) {
                Element categoryEl = doc.createElement("category");
                categoryEl.appendChild(doc.createTextNode(tag.getDisplayName()));
                itemEl.appendChild(categoryEl);
            }

            // write content
            Element descriptionEl = doc.createElement("description");

            String content = article.getSummary();
            if (content == null) {
                content = article.getContent();
            }

            try {
                addXHTML(doc, descriptionEl, content, article.getContentType(),
                        articleUrl);
            } catch (Exception e) {
                throw new FeedException(
                        "Unable to generate description for article with id " + df
                                .format(article.getId()), e);
            }

            itemEl.appendChild(descriptionEl);

            channelEl.appendChild(itemEl);
        }

        if (feedUpdated == null) {
            feedUpdated = new Date();
        }

        pubDateEl.appendChild(doc.createTextNode(formatDate(feedUpdated)));

        root.appendChild(channelEl);

        StringWriter writer = new StringWriter();
        try {
            XmlUtils.prettyPrint(doc, new StreamResult(writer));
        } catch (TransformerException e) {
            throw new FeedException("Unable to generate XML for feed", e);
        }

        return writer.toString();
    }

    @Override
    public String getContentType() {
        return "application/rss+xml";
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
        String text = sdf.format(date);
        return text.substring(0, text.length() - 2) + ":" + text
                .substring(text.length() - 2);
    }

    private void addXHTML(Document doc, Element parent, String text,
                          ContentType contentType, URL articleUrl)
            throws TransformerException, SAXException, IOException {
        String content =
                ContentUtils.formatText(text, articleUrl, contentType, contentFilter);
        parent.appendChild(doc.createTextNode(content));
    }
}
