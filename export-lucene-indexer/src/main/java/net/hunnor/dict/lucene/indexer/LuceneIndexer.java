package net.hunnor.dict.lucene.indexer;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import net.hunnor.dict.lucene.analyzer.PerFieldAnalyzer;
import net.hunnor.dict.lucene.constants.Lucene;
import net.hunnor.dict.lucene.model.Entry;
import net.hunnor.dict.lucene.model.Language;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.NIOFSDirectory;

public class LuceneIndexer {

  private String indexDir;

  private String spellingDir;

  private IndexReader indexReader;

  private IndexWriter indexWriter;

  private SpellChecker spellChecker;

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

  /**
   * Opens the Lucene index reader.
   *
   * @throws IOException when thrown by Lucene
   */
  public void openIndexReader() throws IOException {
    File file = new File(indexDir);
    indexReader = IndexReader.open(new NIOFSDirectory(file));
  }

  /**
   * Closes the Lucene index reader.
   *
   * @throws IOException when thrown by Lucene
   */
  public void closeIndexReader() throws IOException {
    if (indexReader != null) {
      indexReader.close();
    }
  }

  /**
   * Opens the Lucene index writer.
   *
   * @throws IOException when thrown by Lucene
   */
  public void openIndexWriter() throws IOException {
    File file = new File(indexDir);
    Analyzer analyzer = PerFieldAnalyzer.getInstance(Lucene.VERSION);
    IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Lucene.VERSION, analyzer);
    indexWriter = new IndexWriter(new NIOFSDirectory(file), indexWriterConfig);
  }

  /**
   * Closes the Lucene index writer.
   *
   * @throws IOException when thrown by Lucene
   */
  public void closeIndexWriter() throws IOException {
    if (indexWriter != null) {
      indexWriter.close();
    }
  }

  /**
   * Opens the Lucene spell checker.
   *
   * @throws IOException when thrown by Lucene
   */
  public void openSpellChecker() throws IOException {
    File file = new File(spellingDir);
    spellChecker = new SpellChecker(new NIOFSDirectory(file));
  }

  /**
   * Closes the Lucene spell checker.
   *
   * @throws IOException when thrown by Lucene
   */
  public void closeSpellChecker() throws IOException {
    if (spellChecker != null) {
      spellChecker.close();
    }
  }

  /**
   * Create suggestions from the main index.
   *
   * @throws IOException when thrown by Lucene
   */
  public void createSuggestions() throws IOException {
    if (spellChecker != null) {
      Dictionary hungarianDictionary = new LuceneDictionary(indexReader, Lucene.HU_ROOTS_LC);
      Dictionary norwegianDictionary = new LuceneDictionary(indexReader, Lucene.NO_ROOTS_LC);
      Analyzer analyzer = PerFieldAnalyzer.getInstance(Lucene.VERSION);
      IndexWriterConfig indexWriterConfig1 = new IndexWriterConfig(Lucene.VERSION, analyzer);
      IndexWriterConfig indexWriterConfig2 = new IndexWriterConfig(Lucene.VERSION, analyzer);
      spellChecker.indexDictionary(hungarianDictionary, indexWriterConfig1, false);
      spellChecker.indexDictionary(norwegianDictionary, indexWriterConfig2, false);
    }
  }

  /**
   * Writes a single model object to the index.
   *
   * @param indexObject the model object to index
   * @throws IOException when thrown by Lucene
   */
  public void write(Entry indexObject) throws IOException {
    Document luceneDocument = toLuceneDocument(indexObject);
    if (indexWriter != null) {
      indexWriter.addDocument(luceneDocument);
      for (String root : indexObject.getRoots()) {
        Document suggestion = new Document();
        suggestion.add(
            new Field(Lucene.SUGGESTION, root, Field.Store.YES, Field.Index.ANALYZED));
        indexWriter.addDocument(suggestion);
      }
    }
  }

  private Document toLuceneDocument(Entry entry) {

    String rootsField = Lucene.HU_ROOTS;
    String rootsLcField = Lucene.HU_ROOTS_LC;
    String formsField = Lucene.HU_FORMS;
    String transField = Lucene.HU_TRANS;
    String quoteField = Lucene.HU_QUOTE;
    String quoteTransField = Lucene.HU_QUOTETRANS;
    if (Language.NO.equals(entry.getLang())) {
      rootsField = Lucene.NO_ROOTS;
      rootsLcField = Lucene.NO_ROOTS_LC;
      formsField = Lucene.NO_FORMS;
      transField = Lucene.NO_TRANS;
      quoteField = Lucene.NO_QUOTE;
      quoteTransField = Lucene.NO_QUOTETRANS;
    }

    Document document = new Document();

    if (entry.getLang() != null) {
      // Lower case for compatibility with the native Android app
      String lang = entry.getLang().toString().toLowerCase(Locale.getDefault());
      document.add(
          new Field(Lucene.LANG, lang,
              Field.Store.YES, Field.Index.ANALYZED));
    }

    if (entry.getId() != null) {
      document.add(new Field(Lucene.ID, entry.getId(), Field.Store.YES, Field.Index.ANALYZED));
    }

    for (String root : entry.getRoots()) {
      document.add(new Field(rootsField, root, Field.Store.YES, Field.Index.ANALYZED));
      document.add(new Field(rootsLcField, root, Field.Store.YES, Field.Index.ANALYZED));
    }
    for (String form : entry.getForms()) {
      document.add(new Field(formsField, form, Field.Store.NO, Field.Index.ANALYZED));
    }
    for (String tr : entry.getTrans()) {
      document.add(new Field(transField, tr, Field.Store.NO, Field.Index.ANALYZED));
    }
    for (String q : entry.getQuote()) {
      document.add(new Field(quoteField, q, Field.Store.NO, Field.Index.ANALYZED));
    }
    for (String quoteTr : entry.getQuoteTrans()) {
      document.add(new Field(quoteTransField, quoteTr, Field.Store.NO, Field.Index.ANALYZED));
    }

    if (entry.getSort() != null) {
      document.add(new Field(Lucene.SORT, entry.getSort(), Field.Store.YES, Field.Index.ANALYZED));
    }

    if (entry.getText() != null) {
      document.add(
          new Field(Lucene.TEXT, entry.getText(), Field.Store.YES, Field.Index.NOT_ANALYZED));
    }

    return document;

  }

}
