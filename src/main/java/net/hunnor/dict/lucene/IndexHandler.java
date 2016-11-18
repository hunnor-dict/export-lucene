package net.hunnor.dict.lucene;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.hu.HungarianAnalyzer;
import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;

import net.hunnor.dict.lucene.model.Entry;

/**
 * Handles low level operations on the Lucene index.
 */
public class IndexHandler {

	private static final Version LUCENE_VERSION = Version.LUCENE_36;

	private IndexReader indexReader;
	private IndexWriter indexWriter;
	private SpellChecker spellChecker;

	private String indexDir;
	private String spellingDir;

	public void closeIndexReader() throws IOException {
		indexReader.close();
	}

	public void closeIndexWriter() throws IOException {
		indexWriter.close();
	}

	public void closeSpellChecker() throws IOException {
		spellChecker.close();
	}

	public void openIndexReader() throws IOException {
		File file = new File(indexDir);
		Directory directory = new NIOFSDirectory(file);
		indexReader = IndexReader.open(directory);
	}

	public void openIndexWriter() throws IOException {
		File file = new File(indexDir);
		Directory directory = new NIOFSDirectory(file);
		Analyzer analyzer = getAnalyzer();
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(LUCENE_VERSION, analyzer );
		indexWriter = new IndexWriter(directory, indexWriterConfig);
	}

	public void openSpellChecker() throws IOException {
		File file = new File(spellingDir);
		Directory directory = new NIOFSDirectory(file);
		spellChecker = new SpellChecker(directory);
	}

	public Entry read(int id) throws IOException {
		Document document = indexReader.document(id);
		return new Entry(document);
	}

	public void write(Entry indexObject) throws IOException {
		Document luceneDocument = indexObject.toLuceneDocument();
		indexWriter.addDocument(luceneDocument);
	}

	public void createSuggestions() throws IOException {
		Dictionary hungarianDictionary = new LuceneDictionary(
				indexReader, Entry.LUCENE_FIELD_HU_ROOTS);
		Dictionary norwegianDictionary = new LuceneDictionary(
				indexReader, Entry.LUCENE_FIELD_NO_ROOTS);
		Analyzer analyzer = getAnalyzer();
		IndexWriterConfig indexWriterConfig1 =
				new IndexWriterConfig(LUCENE_VERSION, analyzer);
		IndexWriterConfig indexWriterConfig2 =
				new IndexWriterConfig(LUCENE_VERSION, analyzer);
		spellChecker.indexDictionary(hungarianDictionary, indexWriterConfig1, false);
		spellChecker.indexDictionary(norwegianDictionary, indexWriterConfig2, false);
	}

	public String getIndexDir() {
		return indexDir;
	}

	public void setIndexDir(String indexDir) {
		this.indexDir = indexDir;
	}

	public String getSpellingDir() {
		return spellingDir;
	}

	public void setSpellingDir(String spellingDir) {
		this.spellingDir = spellingDir;
	}

	private Analyzer getAnalyzer() {
		// Declare Analyzers
		KeywordAnalyzer keywordAnalyzer = new KeywordAnalyzer();
		CustomAnalyzer customAnalyzer = new CustomAnalyzer(LUCENE_VERSION);
		HungarianAnalyzer hungarianAnalyzer = new HungarianAnalyzer(
				LUCENE_VERSION, CharArraySet.EMPTY_SET);
		NorwegianAnalyzer norwegianAnalyzer = new NorwegianAnalyzer(
				LUCENE_VERSION, CharArraySet.EMPTY_SET);
		// Create mapping
		Map<String, Analyzer> mapping = new HashMap<String, Analyzer>();
		mapping.put(Entry.LUCENE_FIELD_HU_ROOTS, customAnalyzer);
		mapping.put(Entry.LUCENE_FIELD_NO_ROOTS, customAnalyzer);
		mapping.put(Entry.LUCENE_FIELD_HU_FORMS, customAnalyzer);
		mapping.put(Entry.LUCENE_FIELD_NO_FORMS, customAnalyzer);
		mapping.put(Entry.LUCENE_FIELD_HU_TRANS, norwegianAnalyzer);
		mapping.put(Entry.LUCENE_FIELD_NO_TRANS, hungarianAnalyzer);
		mapping.put(Entry.LUCENE_FIELD_HU_QUOTE, hungarianAnalyzer);
		mapping.put(Entry.LUCENE_FIELD_NO_QUOTE, norwegianAnalyzer);
		mapping.put(Entry.LUCENE_FIELD_HU_QUOTETRANS, norwegianAnalyzer);
		mapping.put(Entry.LUCENE_FIELD_NO_QUOTETRANS, hungarianAnalyzer);
		// Create and return Analyzer
		Analyzer analyzer = new PerFieldAnalyzerWrapper(keywordAnalyzer, mapping);
		return analyzer;
	}

}
