package net.hunnor.dict.lucene.analyzer;

import net.hunnor.dict.lucene.constants.Lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.hu.HungarianAnalyzer;
import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.util.Version;

import java.util.HashMap;
import java.util.Map;

public final class PerFieldAnalyzer {

  private PerFieldAnalyzer() {
  }

  /**
   * Return an instance of the custom Lucene analyzer.
   * @param version the Lucene version
   * @return a new instance of the analyzer
   */
  public static Analyzer getInstance(Version version) {

    Map<String, Analyzer> mapping = new HashMap<>();

    FoldingAnalyzer foldingAnalyzer = new FoldingAnalyzer(version);
    mapping.put(Lucene.HU_ROOTS, foldingAnalyzer);
    mapping.put(Lucene.NO_ROOTS, foldingAnalyzer);
    mapping.put(Lucene.HU_FORMS, foldingAnalyzer);
    mapping.put(Lucene.NO_FORMS, foldingAnalyzer);
    mapping.put(Lucene.SORT, foldingAnalyzer);
    mapping.put(Lucene.SUGGESTION, foldingAnalyzer);

    HungarianAnalyzer hungarianAnalyzer =
        new HungarianAnalyzer(version, CharArraySet.EMPTY_SET);
    mapping.put(Lucene.NO_TRANS, hungarianAnalyzer);
    mapping.put(Lucene.HU_QUOTE, hungarianAnalyzer);
    mapping.put(Lucene.NO_QUOTETRANS, hungarianAnalyzer);

    NorwegianAnalyzer norwegianAnalyzer =
        new NorwegianAnalyzer(version, CharArraySet.EMPTY_SET);
    mapping.put(Lucene.HU_TRANS, norwegianAnalyzer);
    mapping.put(Lucene.NO_QUOTE, norwegianAnalyzer);
    mapping.put(Lucene.HU_QUOTETRANS, norwegianAnalyzer);

    KeywordAnalyzer keywordAnalyzer = new KeywordAnalyzer();
    return new PerFieldAnalyzerWrapper(keywordAnalyzer, mapping);

  }

}
