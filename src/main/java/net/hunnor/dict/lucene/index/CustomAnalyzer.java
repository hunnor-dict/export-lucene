package net.hunnor.dict.lucene.index;

import java.io.Reader;

import org.apache.lucene.analysis.ASCIIFoldingFilter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

/**
 * Custom Lucene analyzer. Used for written forms in both languages.
 *
 */
public class CustomAnalyzer extends Analyzer {

	/**
	 * The Lucene version.
	 */
	private final Version version;

	/**
	 * Constructor with the version field.
	 * @param v the Lucene version
	 */
	public CustomAnalyzer(final Version v) {
		this.version = v;
	}

	@Override
	public final TokenStream tokenStream(
			final String field,
			final Reader reader) {
		TokenStream result = new StandardTokenizer(version, reader);
		result = new StandardFilter(version, result);
		result = new LowerCaseFilter(version, result);
		result = new ASCIIFoldingFilter(result);
		return result;
	}

}
