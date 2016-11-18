package net.hunnor.dict.lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.hu.HungarianAnalyzer;
import org.apache.lucene.analysis.no.NorwegianAnalyzer;
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

	/**
	 * Convert a Lucene document to a model object.
	 * @param document the Lucene document to process
	 * @return a new model object with data from the Lucene document
	 */
	public Entry getEntryFromDocument(final Document document) {

		Entry entry = new Entry();

		entry.setLang(document.get(IndexFields.LANG));
		entry.setId(document.get(IndexFields.ID));

		String[] roots = document.getValues(IndexFields.ROOTS);
		if (roots.length > 0) {
			List<String> rootList = new ArrayList<>();
			for (String root: roots) {
				rootList.add(root);
			}
			entry.setRoots(rootList);
		}

		String[] forms = document.getValues(IndexFields.FORMS);
		if (forms.length > 0) {
			List<String> formList = new ArrayList<>();
			for (String form: forms) {
				formList.add(form);
			}
			entry.setForms(formList);
		}

		String[] trans = document.getValues(IndexFields.TRANS);
		if (trans.length > 0) {
			List<String> transList = new ArrayList<>();
			for (String tr: trans) {
				transList.add(tr);
			}
			entry.setTrans(transList);
		}

		String[] quote = document.getValues(IndexFields.QUOTE);
		if (quote.length > 0) {
			List<String> quoteList = new ArrayList<>();
			for (String q: quote) {
				quoteList.add(q);
			}
			entry.setQuote(quoteList);
		}

		String[] quoteTrans = document.getValues(IndexFields.QUOTETRANS);
		if (quoteTrans.length > 0) {
			List<String> quoteTransList = new ArrayList<>();
			for (String qTr: quoteTrans) {
				quoteTransList.add(qTr);
			}
			entry.setQuoteTrans(quoteTransList);
		}

		entry.setText(document.get(IndexFields.TEXT));

		return entry;

	}

	/**
	 * Convert a model object to a Lucene document.
	 * @param entry the domain object to process
	 * @return a new Lucene document with data from the model object
	 */
	public Document toLuceneDocument(final Entry entry) {
		String rootsField = IndexFields.HU_ROOTS;
		String formsField = IndexFields.HU_FORMS;
		String transField = IndexFields.HU_TRANS;
		String quoteField = IndexFields.HU_QUOTE;
		String quoteTransField = IndexFields.HU_QUOTETRANS;
		if ("no".equals(entry.getLang())) {
			rootsField = IndexFields.NO_ROOTS;
			formsField = IndexFields.NO_FORMS;
			transField = IndexFields.NO_TRANS;
			quoteField = IndexFields.NO_QUOTE;
			quoteTransField = IndexFields.NO_QUOTETRANS;
		}

		Document document = new Document();
		if (entry.getLang() != null) {
			document.add(new Field(IndexFields.LANG, entry.getLang(), Field.Store.YES, Field.Index.ANALYZED));
		}
		if (entry.getId() != null) {
			document.add(new Field(IndexFields.ID, entry.getId(), Field.Store.YES, Field.Index.ANALYZED));
		}
		if (entry.getRoots() != null) {
			for (String root: entry.getRoots()) {
				document.add(new Field(rootsField, root, Field.Store.YES, Field.Index.ANALYZED));
			}
		}
		if (entry.getForms() != null) {
			for (String form: entry.getForms()) {
				document.add(new Field(formsField, form, Field.Store.NO, Field.Index.ANALYZED));
			}
		}
		if (entry.getTrans() != null) {
			for (String tr: entry.getTrans()) {
				document.add(new Field(transField, tr, Field.Store.NO, Field.Index.ANALYZED));
			}
		}
		if (entry.getQuote() != null) {
			for (String q: entry.getQuote()) {
				document.add(new Field(quoteField, q, Field.Store.NO, Field.Index.ANALYZED));
			}
		}
		if (entry.getQuoteTrans() != null) {
			for (String qTr: entry.getQuoteTrans()) {
				document.add(new Field(quoteTransField, qTr, Field.Store.NO, Field.Index.ANALYZED));
			}
		}

		if (entry.getText() != null) {
			document.add(new Field(IndexFields.TEXT, entry.getText(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		}

		return document;
	}

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
		return getEntryFromDocument(document);
	}

	public void write(Entry indexObject) throws IOException {
		Document luceneDocument = toLuceneDocument(indexObject);
		indexWriter.addDocument(luceneDocument);
	}

	public void createSuggestions() throws IOException {
		Dictionary hungarianDictionary = new LuceneDictionary(
				indexReader, IndexFields.HU_ROOTS);
		Dictionary norwegianDictionary = new LuceneDictionary(
				indexReader, IndexFields.NO_ROOTS);
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
		mapping.put(IndexFields.HU_ROOTS, customAnalyzer);
		mapping.put(IndexFields.NO_ROOTS, customAnalyzer);
		mapping.put(IndexFields.HU_FORMS, customAnalyzer);
		mapping.put(IndexFields.NO_FORMS, customAnalyzer);
		mapping.put(IndexFields.HU_TRANS, norwegianAnalyzer);
		mapping.put(IndexFields.NO_TRANS, hungarianAnalyzer);
		mapping.put(IndexFields.HU_QUOTE, hungarianAnalyzer);
		mapping.put(IndexFields.NO_QUOTE, norwegianAnalyzer);
		mapping.put(IndexFields.HU_QUOTETRANS, norwegianAnalyzer);
		mapping.put(IndexFields.NO_QUOTETRANS, hungarianAnalyzer);
		// Create and return Analyzer
		Analyzer analyzer = new PerFieldAnalyzerWrapper(keywordAnalyzer, mapping);
		return analyzer;
	}

}
