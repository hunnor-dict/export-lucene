package net.hunnor.dict.lucene;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.hunnor.dict.lucene.indexer.LuceneIndexer;
import net.hunnor.dict.lucene.model.Entry;
import net.hunnor.dict.lucene.parser.StaxParser;

/**
 * The indexer service.
 */
public final class Service {

	/**
	 * Default logger.
	 */
	private static final Logger LOGGER =
			LoggerFactory.getLogger(Service.class);

	/**
	 * Create the main index.
	 * @param file the file to index
	 * @param lang the language to index the file as
	 * @param indexDir the directory to create the index in
	 */
	public void indexFile(
			final String file,
			final String lang,
			final String indexDir) {

		try {

			LuceneIndexer luceneIndexer = new LuceneIndexer();
			luceneIndexer.setIndexDir(indexDir);
			luceneIndexer.openIndexWriter();

			StaxParser staxParser = new StaxParser();
			staxParser.openFile(file);
			while (staxParser.hasNext()) {
				Entry entry = staxParser.next();
				if (entry != null) {
					entry.setLang(lang);
					luceneIndexer.write(entry);
				}
			}

			luceneIndexer.closeIndexWriter();

		} catch (IOException | XMLStreamException e) {
			LOGGER.error(e.getMessage(), e);
		}

	}

	/**
	 * Create the spell checking index from the main index.
	 * @param indexDir the directory with the main index
	 * @param spellcheckDir the directory to create the spell checking index in
	 */
	public void indexSuggestions(
			final String indexDir,
			final String spellcheckDir) {

		try {

			LuceneIndexer luceneIndexer = new LuceneIndexer();

			luceneIndexer.setIndexDir(indexDir);
			luceneIndexer.openIndexReader();
			luceneIndexer.setSpellingDir(spellcheckDir);
			luceneIndexer.openSpellChecker();

			luceneIndexer.createSuggestions();

			luceneIndexer.closeSpellChecker();
			luceneIndexer.closeIndexReader();

		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}		

	}

}
