package net.hunnor.dict.lucene.analyzer;

import static org.junit.Assert.assertEquals;

import net.hunnor.dict.lucene.analyzer.FoldingAnalyzer;
import net.hunnor.dict.lucene.constants.FieldNames;
import net.hunnor.dict.lucene.constants.Lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class FoldingAnalyzerTest {

  @Test
  public void analyzerTest() throws IOException {
    Analyzer analyzer = new FoldingAnalyzer(Lucene.VERSION);
    Reader reader = new StringReader("åaøo; ÆaE");
    TokenStream stream = analyzer.tokenStream(FieldNames.ID, reader);
    stream.incrementToken();
    CharTermAttribute attribute = stream.getAttribute(CharTermAttribute.class);
    assertEquals("aaoo", attribute.toString());
    stream.incrementToken();
    attribute = stream.getAttribute(CharTermAttribute.class);
    assertEquals("aeae", attribute.toString());
    analyzer.close();
  }

}
