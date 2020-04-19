package net.hunnor.dict.lucene.analyzer;

import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public final class FoldingAnalyzer extends Analyzer {

  @Override
  protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
    Tokenizer source = new StandardTokenizer(reader);
    TokenStream filter = new StandardFilter(source);
    filter = new LowerCaseFilter(filter);
    filter = new ASCIIFoldingFilter(filter);
    return new TokenStreamComponents(source, filter);
  }

}
