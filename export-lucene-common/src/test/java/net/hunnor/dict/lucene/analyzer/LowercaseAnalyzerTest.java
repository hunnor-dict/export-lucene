package net.hunnor.dict.lucene.analyzer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import net.hunnor.dict.lucene.constants.Lucene;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.jupiter.api.Test;

class LowercaseAnalyzerTest {

  @Test
  void analyzerTest() throws IOException {

    Analyzer analyzer = new LowercaseAnalyzer(Lucene.VERSION);
    Reader reader = new StringReader("ÁRVÍZTŰRŐ TÜKÖRFÚRÓGÉP BLÅBÆRSYLTETØY");
    TokenStream stream = analyzer.tokenStream(Lucene.ID, reader);

    stream.incrementToken();
    CharTermAttribute attribute = stream.getAttribute(CharTermAttribute.class);
    assertEquals("árvíztűrő", attribute.toString());

    stream.incrementToken();
    attribute = stream.getAttribute(CharTermAttribute.class);
    assertEquals("tükörfúrógép", attribute.toString());

    stream.incrementToken();
    attribute = stream.getAttribute(CharTermAttribute.class);
    assertEquals("blåbærsyltetøy", attribute.toString());

    analyzer.close();

  }

}
