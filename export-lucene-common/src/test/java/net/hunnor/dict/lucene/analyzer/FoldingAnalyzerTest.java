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

class FoldingAnalyzerTest {

  @Test
  void analyzerTest() throws IOException {

    Analyzer analyzer = new FoldingAnalyzer();
    Reader reader = new StringReader("åaøo; ÆaE");
    TokenStream stream = analyzer.tokenStream(Lucene.ID, reader);

    stream.reset();

    stream.incrementToken();
    CharTermAttribute attribute = stream.getAttribute(CharTermAttribute.class);
    assertEquals("aaoo", attribute.toString());

    stream.incrementToken();
    attribute = stream.getAttribute(CharTermAttribute.class);
    assertEquals("aeae", attribute.toString());

    stream.end();
    stream.close();

    analyzer.close();

  }

}
