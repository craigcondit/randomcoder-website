package org.randomcoder.content;

import org.junit.Test;
import org.randomcoder.test.mock.content.ContentFilterMock;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class ContentUtilsTest {
  private static final String SOURCE = "Line 1\r\nLine 2";
  private static final String RESULT =
      "<div class=\"text-plain\">Line 1<br/>Line 2</div>";

  @Test public void testFormat() throws Exception {
    String result = ContentUtils
        .format("text/plain", null, new InputSource(new StringReader(SOURCE)),
            new TextFilter());
    result = result.replaceAll("\r", "");
    result = result.replaceAll("\n", "");
    assertEquals(RESULT, result);
  }

  @Test public void testFormatText() throws Exception {
    String result = ContentUtils
        .formatText(SOURCE, null, ContentType.TEXT, new TextFilter());
    result = result.replaceAll("\r", "");
    result = result.replaceAll("\n", "");
    assertEquals(RESULT, result);
  }

  @Test public void testFormatNoTemplates() throws Exception {
    ContentUtils
        .format("bogus", null, new InputSource(new StringReader(SOURCE)),
            new ContentFilterMock());
    ContentUtils
        .formatText("bogus", null, ContentType.TEXT, new ContentFilterMock());
  }

  @Test public void testFormatTextNoTemplates() throws Exception {
    ContentUtils
        .formatText("bogus", null, ContentType.TEXT, new ContentFilterMock());
  }

  @Test public void testFormatTextWithPrefixes() throws Exception {
    ContentUtils.formatText("text", null, ContentType.XHTML, new XHTMLFilter(Collections.emptySet()));
  }
}
