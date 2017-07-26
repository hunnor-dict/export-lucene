package net.hunnor.dict.lucene.test.indexer.analyzer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.junit.Test;

import net.hunnor.dict.lucene.indexer.analyzer.FoldingAnalyzer;

/**
 * Tests for the custom Lucene analyzer.
 */
public final class FoldingAnalyzerTest {

	/**
	 * Test for the constructor with the version field.
	 * @throws IOException 
	 */
	@Test
	public void analyzerTest() throws IOException {
		Analyzer analyzer = new FoldingAnalyzer(Version.LUCENE_36);
		Reader reader = new StringReader("åaøo; ÆaE");
		TokenStream stream = analyzer.tokenStream("id", reader);
		stream.incrementToken();
		CharTermAttribute attribute =
				stream.getAttribute(CharTermAttribute.class);
		assertEquals("aaoo", attribute.toString());
		stream.incrementToken();
		attribute = stream.getAttribute(CharTermAttribute.class);
		assertEquals("aeae", attribute.toString());
		analyzer.close();
	}

}
