package net.hunnor.dict.lucene.indexer;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;

import net.hunnor.dict.lucene.indexer.analyzer.PerFieldAnalyzer;
import net.hunnor.dict.lucene.model.Entry;

/**
 * Handles low level operations on the Lucene index.
 */
public final class LuceneIndexer {

	/**
	 * The directory of the main index.
	 */
	private String indexDir;

	/**
	 * The directory of the spell checking index.
	 */
	private String spellingDir;

	/**
	 * Constant for the Lucene version.
	 */
	private static final Version LUCENE_VERSION = Version.LUCENE_36;

	/**
	 * Lucene index reader.
	 */
	private IndexReader indexReader;

	/**
	 * Lucene index writer.
	 */
	private IndexWriter indexWriter;

	/**
	 * Lucene spell checker.
	 */
	private SpellChecker spellChecker;

	/**
	 * Return the directory for the main index.
	 * @return the directory for the main index
	 */
	public String getIndexDir() {
		return indexDir;
	}

	/**
	 * Set the directory for the main index.
	 * @param dir the directory for the main index
	 */
	public void setIndexDir(final String dir) {
		this.indexDir = dir;
	}

	/**
	 * Return the directory for the spell checking index.
	 * @return the directory for the spell checking index
	 */
	public String getSpellingDir() {
		return spellingDir;
	}

	/**
	 * Set the directory for the spell checking index.
	 * @param dir the directory for the spell checking index
	 */
	public void setSpellingDir(final String dir) {
		this.spellingDir = dir;
	}

	/**
	 * Opens the Lucene index reader.
	 * @throws IOException when thrown by Lucene
	 */
	public void openIndexReader() throws IOException {
		File file = new File(indexDir);
		Directory directory = new NIOFSDirectory(file);
		indexReader = IndexReader.open(directory);
	}

	/**
	 * Closes the Lucene index reader.
	 * @throws IOException when thrown by Lucene
	 */
	public void closeIndexReader() throws IOException {
		if (indexReader != null) {
			indexReader.close();
		}
	}

	/**
	 * Opens the Lucene index writer.
	 * @throws IOException when thrown by Lucene
	 */
	public void openIndexWriter() throws IOException {
		File file = new File(indexDir);
		Directory directory = new NIOFSDirectory(file);
		Analyzer analyzer = PerFieldAnalyzer.getInstance(LUCENE_VERSION);
		IndexWriterConfig indexWriterConfig =
				new IndexWriterConfig(LUCENE_VERSION, analyzer);
		indexWriter = new IndexWriter(directory, indexWriterConfig);
	}

	/**
	 * Closes the Lucene index writer.
	 * @throws IOException when thrown by Lucene
	 */
	public void closeIndexWriter() throws IOException {
		if (indexWriter != null) {
			indexWriter.close();
		}
	}

	/**
	 * Opens the Lucene spell checker.
	 * @throws IOException when thrown by Lucene
	 */
	public void openSpellChecker() throws IOException {
		File file = new File(spellingDir);
		Directory directory = new NIOFSDirectory(file);
		spellChecker = new SpellChecker(directory);
	}

	/**
	 * Closes the Lucene spell checker.
	 * @throws IOException when thrown by Lucene
	 */
	public void closeSpellChecker() throws IOException {
		if (spellChecker != null) {
			spellChecker.close();
		}
	}

	/**
	 * Create suggestions from the main index.
	 * @throws IOException when thrown by Lucene
	 */
	public void createSuggestions() throws IOException {
		Dictionary hungarianDictionary = new LuceneDictionary(
				indexReader, FieldNames.HU_ROOTS);
		Dictionary norwegianDictionary = new LuceneDictionary(
				indexReader, FieldNames.NO_ROOTS);
		Analyzer analyzer = PerFieldAnalyzer.getInstance(LUCENE_VERSION);
		IndexWriterConfig indexWriterConfig1 =
				new IndexWriterConfig(LUCENE_VERSION, analyzer);
		IndexWriterConfig indexWriterConfig2 =
				new IndexWriterConfig(LUCENE_VERSION, analyzer);
		if (spellChecker != null) {
			spellChecker.indexDictionary(
					hungarianDictionary, indexWriterConfig1, false);
			spellChecker.indexDictionary(
					norwegianDictionary, indexWriterConfig2, false);
		}
	}

	/**
	 * Writes a single model object to the index.
	 * @param indexObject the model object to index
	 * @throws IOException when thrown by Lucene
	 */
	public void write(final Entry indexObject) throws IOException {
		Document luceneDocument = toLuceneDocument(indexObject);
		if (indexWriter != null) {
			indexWriter.addDocument(luceneDocument);
		}
	}

	/**
	 * Convert a model object to a Lucene document.
	 * @param entry the domain object to process
	 * @return a new Lucene document with data from the model object
	 */
	private Document toLuceneDocument(final Entry entry) {

		String rootsField = FieldNames.HU_ROOTS;
		String formsField = FieldNames.HU_FORMS;
		String transField = FieldNames.HU_TRANS;
		String quoteField = FieldNames.HU_QUOTE;
		String quoteTransField = FieldNames.HU_QUOTETRANS;
		if ("no".equals(entry.getLang())) {
			rootsField = FieldNames.NO_ROOTS;
			formsField = FieldNames.NO_FORMS;
			transField = FieldNames.NO_TRANS;
			quoteField = FieldNames.NO_QUOTE;
			quoteTransField = FieldNames.NO_QUOTETRANS;
		}

		Document document = new Document();

		if (entry.getLang() != null) {
			document.add(new Field(FieldNames.LANG, entry.getLang(),
					Field.Store.YES, Field.Index.ANALYZED));
		}
		if (entry.getId() != null) {
			document.add(new Field(FieldNames.ID, entry.getId(),
					Field.Store.YES, Field.Index.ANALYZED));
		}
		for (String root: entry.getRoots()) {
			document.add(new Field(rootsField, root,
					Field.Store.YES, Field.Index.ANALYZED));
		}
		for (String form: entry.getForms()) {
			document.add(new Field(formsField, form,
					Field.Store.NO, Field.Index.ANALYZED));
		}
		for (String tr: entry.getTrans()) {
			document.add(new Field(transField, tr,
					Field.Store.NO, Field.Index.ANALYZED));
		}
		for (String q: entry.getQuote()) {
			document.add(new Field(quoteField, q,
					Field.Store.NO, Field.Index.ANALYZED));
		}
		for (String qTr: entry.getQuoteTrans()) {
			document.add(new Field(quoteTransField, qTr,
					Field.Store.NO, Field.Index.ANALYZED));
		}

		if (entry.getText() != null) {
			document.add(new Field(FieldNames.TEXT, entry.getText(),
					Field.Store.YES, Field.Index.NOT_ANALYZED));
		}

		return document;

	}

}
