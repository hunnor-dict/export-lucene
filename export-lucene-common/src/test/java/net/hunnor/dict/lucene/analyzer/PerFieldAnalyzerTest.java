package net.hunnor.dict.lucene.analyzer;

import static org.junit.Assert.assertNotNull;

import net.hunnor.dict.lucene.analyzer.PerFieldAnalyzer;
import net.hunnor.dict.lucene.constants.Lucene;

import org.apache.lucene.analysis.Analyzer;
import org.junit.Test;

public class PerFieldAnalyzerTest {

  @Test
  public void testGetInstance() {
    Analyzer analyzer = PerFieldAnalyzer.getInstance(Lucene.VERSION);
    assertNotNull(analyzer);
  }

}
