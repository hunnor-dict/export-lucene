package net.hunnor.dict.lucene;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.hunnor.dict.lucene.index.IndexHandler;
import net.hunnor.dict.lucene.model.Entry;

/**
 * The indexer service.
 */
public class Indexer {

	/**
	 * Default logger.
	 */
	private static final Logger LOGGER =
			LoggerFactory.getLogger(Indexer.class);

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

			IndexHandler indexHandler = new IndexHandler();
			indexHandler.setIndexDir(indexDir);
			indexHandler.openIndexWriter();

			Parser parser = new Parser();
			parser.openFile(file, lang);
			while (parser.hasNext()) {
				Entry entry = parser.next();
				if (entry != null) {
					entry.setLang(lang);
					indexHandler.write(entry);
				}
			}

			indexHandler.closeIndexWriter();

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

			IndexHandler indexHandler = new IndexHandler();

			indexHandler.setIndexDir(indexDir);
			indexHandler.openIndexReader();
			indexHandler.setSpellingDir(spellcheckDir);
			indexHandler.openSpellChecker();

			indexHandler.createSuggestions();

			indexHandler.closeSpellChecker();
			indexHandler.closeIndexReader();

		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}		

	}
	
}
