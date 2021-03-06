package net.hunnor.dict.lucene.analyzer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import net.hunnor.dict.lucene.constants.Lucene;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.jupiter.api.Test;

class PerFieldAnalyzerTest {

  @Test
  void testGetInstance() {
    Analyzer analyzer = PerFieldAnalyzer.getInstance(Lucene.VERSION);
    assertNotNull(analyzer);
  }

  @Test
  void testHungarianAnalyzer() throws IOException {
    Analyzer analyzer = PerFieldAnalyzer.getInstance(Lucene.VERSION);
    Reader reader = new StringReader("beszélünk");
    TokenStream stream = analyzer.tokenStream(Lucene.NO_TRANS, reader);
    stream.incrementToken();
    CharTermAttribute attribute = stream.getAttribute(CharTermAttribute.class);
    assertEquals("beszél", attribute.toString());
  }

  @Test
  void testNorwegianAnalyzer() throws IOException {
    Analyzer analyzer = PerFieldAnalyzer.getInstance(Lucene.VERSION);
    Reader reader = new StringReader("bilens");
    TokenStream stream = analyzer.tokenStream(Lucene.HU_TRANS, reader);
    stream.incrementToken();
    CharTermAttribute attribute = stream.getAttribute(CharTermAttribute.class);
    assertEquals("bil", attribute.toString());
  }

}
