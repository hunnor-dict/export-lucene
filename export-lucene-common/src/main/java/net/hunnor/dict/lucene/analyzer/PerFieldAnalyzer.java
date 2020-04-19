package net.hunnor.dict.lucene.analyzer;

import java.util.HashMap;
import java.util.Map;
import net.hunnor.dict.lucene.constants.Lucene;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.hu.HungarianAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;

public final class PerFieldAnalyzer {

  private PerFieldAnalyzer() {
  }

  /**
   * Return an instance of the custom Lucene analyzer.
   * @return a new instance of the analyzer
   */
  public static Analyzer getInstance() {

    Map<String, Analyzer> mapping = new HashMap<>();

    FoldingAnalyzer foldingAnalyzer = new FoldingAnalyzer();
    LowercaseAnalyzer lowercaseAnalyzer = new LowercaseAnalyzer();

    mapping.put(Lucene.HU_ROOTS, foldingAnalyzer);
    mapping.put(Lucene.HU_ROOTS_LC, lowercaseAnalyzer);
    mapping.put(Lucene.NO_ROOTS, foldingAnalyzer);
    mapping.put(Lucene.NO_ROOTS_LC, lowercaseAnalyzer);
    mapping.put(Lucene.HU_FORMS, foldingAnalyzer);
    mapping.put(Lucene.NO_FORMS, foldingAnalyzer);
    mapping.put(Lucene.SORT, foldingAnalyzer);
    mapping.put(Lucene.SUGGESTION, foldingAnalyzer);

    HungarianAnalyzer hungarianAnalyzer =
        new HungarianAnalyzer(CharArraySet.EMPTY_SET);
    mapping.put(Lucene.NO_TRANS, hungarianAnalyzer);
    mapping.put(Lucene.HU_QUOTE, hungarianAnalyzer);
    mapping.put(Lucene.NO_QUOTETRANS, hungarianAnalyzer);

    NorwegianAnalyzer norwegianAnalyzer =
        new NorwegianAnalyzer(CharArraySet.EMPTY_SET);
    mapping.put(Lucene.HU_TRANS, norwegianAnalyzer);
    mapping.put(Lucene.NO_QUOTE, norwegianAnalyzer);
    mapping.put(Lucene.HU_QUOTETRANS, norwegianAnalyzer);

    KeywordAnalyzer keywordAnalyzer = new KeywordAnalyzer();
    return new PerFieldAnalyzerWrapper(keywordAnalyzer, mapping);

  }

}
