package net.hunnor.dict.lucene.indexer.analyzer;

import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.hu.HungarianAnalyzer;
import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.util.Version;

import net.hunnor.dict.lucene.indexer.FieldNames;

/**
 * Custom analyzer based on PerFieldAnalyzerWrapper.
 */
public final class PerFieldAnalyzer {

	/**
	 * Hide default constructor.
	 */
	private PerFieldAnalyzer() {
	}

	/**
	 * Return an instance of the custom analyzer.
	 * @param version the Lucene version
	 * @return a new instance of the custom analyzer
	 */
	public static Analyzer getInstance(final Version version) {

		KeywordAnalyzer keywordAnalyzer = new KeywordAnalyzer();
		FoldingAnalyzer customAnalyzer = new FoldingAnalyzer(version);
		HungarianAnalyzer hungarianAnalyzer = new HungarianAnalyzer(
				version, CharArraySet.EMPTY_SET);
		NorwegianAnalyzer norwegianAnalyzer = new NorwegianAnalyzer(
				version, CharArraySet.EMPTY_SET);

		Map<String, Analyzer> mapping = new HashMap<String, Analyzer>();
		mapping.put(FieldNames.HU_ROOTS, customAnalyzer);
		mapping.put(FieldNames.NO_ROOTS, customAnalyzer);
		mapping.put(FieldNames.HU_FORMS, customAnalyzer);
		mapping.put(FieldNames.NO_FORMS, customAnalyzer);
		mapping.put(FieldNames.HU_TRANS, norwegianAnalyzer);
		mapping.put(FieldNames.NO_TRANS, hungarianAnalyzer);
		mapping.put(FieldNames.HU_QUOTE, hungarianAnalyzer);
		mapping.put(FieldNames.NO_QUOTE, norwegianAnalyzer);
		mapping.put(FieldNames.HU_QUOTETRANS, norwegianAnalyzer);
		mapping.put(FieldNames.NO_QUOTETRANS, hungarianAnalyzer);

		Analyzer analyzer = new PerFieldAnalyzerWrapper(
				keywordAnalyzer, mapping);

		return analyzer;

	}

}
