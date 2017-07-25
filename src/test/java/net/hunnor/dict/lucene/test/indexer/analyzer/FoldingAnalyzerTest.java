package net.hunnor.dict.lucene.test.indexer.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.util.Version;
import org.junit.Test;

import net.hunnor.dict.lucene.indexer.analyzer.FoldingAnalyzer;

/**
 * Tests for the custom Lucene analyzer.
 */
public final class FoldingAnalyzerTest {

	/**
	 * Test for the constructor with the version field.
	 */
	@Test
	public void analyzerTest() {
		Analyzer analyzer = new FoldingAnalyzer(Version.LUCENE_36);
		analyzer.close();
	}

}
