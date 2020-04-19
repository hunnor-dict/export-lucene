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
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.BytesRef;

public class LuceneIndexer {

  private String indexDir;

  private IndexReader indexReader;

  private IndexWriter indexWriter;

  public String getIndexDir() {
    return indexDir;
  }

  public void setIndexDir(String indexDir) {
    this.indexDir = indexDir;
  }

  /**
   * Opens the Lucene index reader.
   *
   * @throws IOException when thrown by Lucene
   */
  public void openIndexReader() throws IOException {
    File file = new File(indexDir);
    indexReader = DirectoryReader.open(new NIOFSDirectory(file.toPath()));
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
    Analyzer analyzer = PerFieldAnalyzer.getInstance();
    IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
    indexWriter = new IndexWriter(new NIOFSDirectory(file.toPath()), indexWriterConfig);
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
            new SortedDocValuesField(Lucene.SORT, new BytesRef(root)));
        suggestion.add(
            new TextField(Lucene.SUGGESTION, root, Field.Store.YES));
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
          new StringField(Lucene.LANG, lang,
              Field.Store.YES));
    }

    if (entry.getId() != null) {
      document.add(new StringField(Lucene.ID, entry.getId(), Field.Store.YES));
    }

    for (String root : entry.getRoots()) {
      document.add(new TextField(rootsField, root, Field.Store.YES));
      document.add(new TextField(rootsLcField, root, Field.Store.YES));
    }
    for (String form : entry.getForms()) {
      document.add(new TextField(formsField, form, Field.Store.NO));
    }
    for (String tr : entry.getTrans()) {
      document.add(new TextField(transField, tr, Field.Store.NO));
    }
    for (String q : entry.getQuote()) {
      document.add(new TextField(quoteField, q, Field.Store.NO));
    }
    for (String quoteTr : entry.getQuoteTrans()) {
      document.add(new TextField(quoteTransField, quoteTr, Field.Store.NO));
    }

    if (entry.getSort() != null) {
      document.add(new SortedDocValuesField(Lucene.SORT, new BytesRef(entry.getSort())));
    }

    if (entry.getText() != null) {
      document.add(
          new TextField(Lucene.TEXT, entry.getText(), Field.Store.YES));
    }

    return document;

  }

}
