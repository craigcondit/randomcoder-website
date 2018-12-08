package org.randomcoder.content;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.transform.Templates;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;

/**
 * Content filtering interface.
 *
 * <pre>
 * Copyright (c) 2006, Craig Condit. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * </pre>
 */
public interface ContentFilter {

  /**
   * Validates the given input.
   *
   * @param contentType content type
   * @param content     textual content to validate
   * @throws InvalidContentException     if content is invalid
   * @throws InvalidContentTypeException if the content type is invalid
   * @throws IOException                 if content could not be read
   */
  public void validate(String contentType, Reader content)
      throws InvalidContentException, InvalidContentTypeException, IOException;

  /**
   * Gets an XML reader suitable for the given content type.
   *
   * @param baseUrl     base URL for content, or <code>null</code> to omit
   * @param contentType content type of input
   * @return XML reader
   * @throws SAXException if a SAX parsing error occurs
   */
  public XMLReader getXMLReader(URL baseUrl, String contentType)
      throws SAXException;

  /**
   * Gets XSL templates for the given content type.
   *
   * @param contentType content type of input
   * @return templates, or null if no transformation is to be done
   */
  public Templates getXSLTemplates(String contentType);

  /**
   * Gets the data to prepend to a content stream before processing.
   *
   * @param contentType content type
   * @return prefix data or null if none
   */
  public String getPrefix(String contentType);

  /**
   * Gets the data to append to a content stream before processing.
   *
   * @param contentType content type
   * @return suffixs data or null if none
   */
  public String getSuffix(String contentType);
}
