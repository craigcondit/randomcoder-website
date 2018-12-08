package org.randomcoder.content;

import org.springframework.beans.factory.annotation.Required;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.transform.Templates;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.Map;

/**
 * Content filter wrapper which maps content types onto concrete filter
 * implementations.
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
public class MultiContentFilter implements ContentFilter {
  private Map<String, ContentFilter> filters;
  private ContentFilter defaultFilter;

  /**
   * Sets a map of filters keyed by content type to use.
   *
   * @param filters filter map
   */
  @Required public void setFilters(Map<String, ContentFilter> filters) {
    this.filters = filters;
  }

  /**
   * Sets the default filter to use if lookup fails.
   *
   * @param defaultFilter content filter
   */
  public void setDefaultHandler(ContentFilter defaultFilter) {
    this.defaultFilter = defaultFilter;
  }

  @Override public void validate(String contentType, Reader content)
      throws InvalidContentException, InvalidContentTypeException, IOException {
    getFilterForContentType(contentType).validate(contentType, content);
  }

  @Override public XMLReader getXMLReader(URL baseUrl, String contentType)
      throws SAXException {
    return getFilterForContentType(contentType)
        .getXMLReader(baseUrl, contentType);
  }

  @Override public Templates getXSLTemplates(String contentType) {
    return getFilterForContentType(contentType).getXSLTemplates(contentType);
  }

  @Override public String getPrefix(String contentType) {
    return getFilterForContentType(contentType).getPrefix(contentType);
  }

  @Override public String getSuffix(String contentType) {
    return getFilterForContentType(contentType).getSuffix(contentType);
  }

  private ContentFilter getFilterForContentType(String contentType)
      throws InvalidContentTypeException {
    ContentFilter filter = filters.get(contentType);
    if (filter != null)
      return filter;

    if (defaultFilter == null)
      throw new InvalidContentTypeException(
          "Unknown content type " + contentType);

    return defaultFilter;
  }

}
