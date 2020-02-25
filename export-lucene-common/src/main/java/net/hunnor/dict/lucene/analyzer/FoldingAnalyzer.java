package net.hunnor.dict.lucene.analyzer;

import java.io.Reader;
import org.apache.lucene.analysis.ASCIIFoldingFilter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

public final class FoldingAnalyzer extends Analyzer {

  private Version version;

  public FoldingAnalyzer(Version version) {
    this.version = version;
  }

  @Override
  public TokenStream tokenStream(String field, Reader reader) {
    TokenStream result = new StandardTokenizer(version, reader);
    result = new StandardFilter(version, result);
    result = new LowerCaseFilter(version, result);
    result = new ASCIIFoldingFilter(result);
    return result;
  }

}
