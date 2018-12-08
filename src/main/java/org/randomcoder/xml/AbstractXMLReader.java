package org.randomcoder.xml;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract base class for {@link XMLReader} implementations.
 */
abstract public class AbstractXMLReader implements XMLReader {
  private final Map<String, Boolean> features = new HashMap<String, Boolean>();
  private final Map<String, Object> properties = new HashMap<String, Object>();

  private ContentHandler contentHandler;
  private DTDHandler dtdHandler;
  private EntityResolver entityResolver;
  private ErrorHandler errorHandler;

  /**
   * Parses the given input source.
   *
   * <p>
   * Subclasses must implement this method to handle XML parsing.
   * </p>
   *
   * @param input input source to parse
   * @throws IOException  if resource cannot be read
   * @throws SAXException if an error occurs
   * @see XMLReader#parse(InputSource)
   */
  @Override abstract public void parse(InputSource input)
      throws IOException, SAXException;

  /**
   * Parses a document using the given system id.
   *
   * @param systemId system id
   * @throws IOException  if resource cannot be read
   * @throws SAXException if an error occurs
   */
  @Override public void parse(String systemId)
      throws IOException, SAXException {
    parse(new InputSource(systemId));
  }

  /**
   * Gets the content handler defined for this reader.
   *
   * @return content handler
   */
  @Override public ContentHandler getContentHandler() {
    return contentHandler;
  }

  /**
   * Sets the content handler for this reader.
   *
   * @param contentHandler content handler
   */
  @Override public void setContentHandler(ContentHandler contentHandler) {
    this.contentHandler = contentHandler;
  }

  /**
   * Gets the DTD handler for this reader.
   *
   * @return DTD handler
   */
  @Override public DTDHandler getDTDHandler() {
    return dtdHandler;
  }

  /**
   * Sets the DTD handler for this reader.
   *
   * @param dtdHandler DTD handler
   */
  @Override public void setDTDHandler(DTDHandler dtdHandler) {
    this.dtdHandler = dtdHandler;
  }

  /**
   * Gets the entity resolver for this reader.
   *
   * @return entity resolver
   */
  @Override public EntityResolver getEntityResolver() {
    return entityResolver;
  }

  /**
   * Sets the entity resolver for this reader.
   *
   * @param entityResolver entity resolver
   */
  @Override public void setEntityResolver(EntityResolver entityResolver) {
    this.entityResolver = entityResolver;
  }

  /**
   * Gets the error handler for this reader.
   *
   * @return error handler
   */
  @Override public ErrorHandler getErrorHandler() {
    return errorHandler;
  }

  /**
   * Sets the error handler for this reader.
   *
   * @param errorHandler error handler
   */
  @Override public void setErrorHandler(ErrorHandler errorHandler) {
    this.errorHandler = errorHandler;
  }

  /**
   * Determines if this reader supports the given feature.
   *
   * @param name feature name
   * @return true if feature is available, false otherwise
   * @throws SAXNotRecognizedException if the feature is not recognized
   * @throws SAXNotSupportedException  if the method is not supported
   */
  @Override public boolean getFeature(String name)
      throws SAXNotRecognizedException, SAXNotSupportedException {
    Boolean exists = features.get(name);
    if (exists == null) {
      return false;
    }
    return exists.booleanValue();
  }

  /**
   * Sets the availability of the given feature.
   *
   * @param name  feature name
   * @param value true if enabled, false otherwise
   * @throws SAXNotRecognizedException if the feature is not recognized
   * @throws SAXNotSupportedException  if the method is not supported
   */
  @Override public void setFeature(String name, boolean value)
      throws SAXNotRecognizedException, SAXNotSupportedException {
    features.put(name, value);
  }

  /**
   * Gets the value of the named property.
   *
   * @param name property name
   * @return property value
   * @throws SAXNotRecognizedException if the property is not recognized
   * @throws SAXNotSupportedException  if the method is not supported
   */
  @Override public Object getProperty(String name)
      throws SAXNotRecognizedException, SAXNotSupportedException {
    return properties.get(name);
  }

  /**
   * Sets the value o fthe named property.
   *
   * @param name  property name
   * @param value property value
   * @throws SAXNotRecognizedException if the property is not recognized
   * @throws SAXNotSupportedException  if the method is not supported
   */
  @Override public void setProperty(String name, Object value)
      throws SAXNotRecognizedException, SAXNotSupportedException {
    properties.put(name, value);
  }
}
