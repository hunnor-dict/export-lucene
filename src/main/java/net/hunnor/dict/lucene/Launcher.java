package net.hunnor.dict.lucene;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main class of the Lucene indexer.
 *
 */
public final class Launcher {

	/**
	 * Default logger.
	 */
	private static final Logger LOGGER =
			LoggerFactory.getLogger(Launcher.class);

	/**
	 * The option name for specifying the XML document.
	 */
	private static final String OPTS_XML = "x";

	/**
	 * The option name for specifying the language.
	 */
	private static final String OPTS_LANG = "l";

	/**
	 * The option name for specifying
	 * the directory for the spell checking index.
	 */
	private static final String OPTS_SPELLCHECK_INDEX_DIR = "s";

	/**
	 * The option name for specifying the directory for the index.
	 */
	private static final String OPTS_INDEX_DIR = "d";

	/**
	 * Hide default constructor.
	 */
	private Launcher() {
	}

	/**
	 * The main method of the application.
	 * @param args command line parameters
	 */
	public static void main(final String[] args) {

		Options options = new Options();
		options.addOption(OPTS_XML, true,
				"the XML file to parse");
		options.addOption(OPTS_LANG, true,
				"the language to index the file as ('hu' or 'no')");
		options.addOption(OPTS_INDEX_DIR, true,
				"the directory for the main index");
		options.addOption(OPTS_SPELLCHECK_INDEX_DIR, true,
				"the directory for the spell checking index");

		CommandLineParser parser = new DefaultParser();
		CommandLine commandLine;
		try {
			commandLine = parser.parse(options, args);
		} catch (ParseException e) {
			LOGGER.error(e.getMessage(), e);
			return;
		}

		if (commandLine.hasOption(OPTS_XML)
				&& commandLine.hasOption(OPTS_LANG)
				&& commandLine.hasOption(OPTS_INDEX_DIR)
				&& commandLine.hasOption(OPTS_SPELLCHECK_INDEX_DIR)) {
			indexFile(
					commandLine.getOptionValue(OPTS_XML),
					commandLine.getOptionValue(OPTS_LANG),
					commandLine.getOptionValue(OPTS_INDEX_DIR));
			indexSuggestions(
					commandLine.getOptionValue(OPTS_INDEX_DIR),
					commandLine.getOptionValue(OPTS_SPELLCHECK_INDEX_DIR));
		} else {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -jar export-lucene.jar", options);
		}

		System.exit(0);

	}

	/**
	 * Create the main index.
	 * @param file the file to index
	 * @param lang the language to index the file as
	 * @param indexDir the directory to create the index in
	 */
	private static void indexFile(
			final String file,
			final String lang,
			final String indexDir) {
		try {
			DictionaryParser dictionaryParser = new DictionaryParser();
			dictionaryParser.openIndexWriter(indexDir);
			dictionaryParser.parseFile(file, lang);
			dictionaryParser.closeIndexWriter();
		} catch (XMLStreamException | IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * Create the spell checking index from the main index.
	 * @param indexDir the directory with the main index
	 * @param spellcheckDir the directory to create the spell checking index in
	 */
	private static void indexSuggestions(
			final String indexDir,
			final String spellcheckDir) {
		try {
			DictionaryParser dictionaryParser = new DictionaryParser();
			dictionaryParser.openIndexReader(indexDir);
			dictionaryParser.openSpellChecker(spellcheckDir);
			dictionaryParser.createSuggestions();
			dictionaryParser.closeSpellChecker();
			dictionaryParser.closeIndexReader();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}		
	}

}
