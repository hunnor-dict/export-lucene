package net.hunnor.dict.lucene.searcher;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import net.hunnor.dict.lucene.analyzer.PerFieldAnalyzer;
import net.hunnor.dict.lucene.constants.Lucene;
import net.hunnor.dict.lucene.model.Entry;
import net.hunnor.dict.lucene.model.Language;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.NIOFSDirectory;

public class LuceneSearcher {

  private static LuceneSearcher instance = new LuceneSearcher();

  private IndexReader indexReader;

  private SpellChecker spellChecker;

  private Analyzer analyzer = PerFieldAnalyzer.getInstance(Lucene.VERSION);

  /**
   * Get the single instance of the class.
   * @return the single instance of the class
   */
  public static LuceneSearcher getInstance() {
    return instance;
  }

  public boolean isOpen() {
    return indexReader != null;
  }

  public boolean isSpellCheckerOpen() {
    return spellChecker != null;
  }

  public void open(File indexDirectory) throws IOException {
    NIOFSDirectory directory = new NIOFSDirectory(indexDirectory);
    indexReader = IndexReader.open(directory);
  }

  /**
   * Open a spell checker if the directory exists.
   * @param spellingDirectory the spell checker directory
   * @throws IOException if the directory cannot be opened
   */
  public void openSpellChecker(File spellingDirectory) throws IOException {
    if (spellingDirectory.canRead()) {
      // The searcher module should only use an existing spelling index
      File[] files = spellingDirectory.listFiles();
      if (files != null && files.length > 0) {
        NIOFSDirectory directory = new NIOFSDirectory(spellingDirectory);
        spellChecker = new SpellChecker(directory);
      }
    }
  }

  /**
   * Close the index reader.
   * @throws IOException if there is a low-level IO error
   */
  public void close() throws IOException {
    if (indexReader != null) {
      indexReader.close();
      indexReader = null;
    }
  }

  /**
   * Close the spell checker.
   * @throws IOException if there is a low-level IO error
   */
  public void closeSpellChecker() throws IOException {
    if (spellChecker != null) {
      spellChecker.close();
      spellChecker = null;
    }
  }

  /**
   * Suggest terms from the index.
   * @param userQuery the user query to return suggestions for
   * @param max the maximum number of suggestions to return
   * @return a set of matching terms
   * @throws IOException if there is a low-level IO error
   */
  public List<String> suggestions(String userQuery, int max) throws IOException {
    Query query = createQueryFromFields(userQuery, new String[] {Lucene.SUGGESTION}, true);
    SortField sortField = new SortField(Lucene.SUGGESTION, SortField.STRING);
    Sort sort = new Sort(sortField);
    List<Document> documents = docsFromQuery(query, sort, max);
    List<String> suggestions = new ArrayList<>();
    for (Document document : documents) {
      String suggestion = document.get(Lucene.SUGGESTION);
      if (!suggestions.contains(suggestion)) {
        suggestions.add(suggestion);
      }
    }
    return suggestions;
  }

  /**
   * Suggest terms similar to the user query, using Lucene's spell checker.
   * @param userQuery the user query to return suggestions for
   * @param max the maximum number of spelling suggestions to return
   * @return a set of terms returned by the spell checker
   * @throws IOException if there is a low-level IO error
   */
  public List<String> spellingSuggestions(String userQuery, int max) throws IOException {
    List<String> results = new ArrayList<>();
    String[] suggestions = executeSuggestion(userQuery, max);
    results.addAll(Arrays.asList(suggestions));
    return results;
  }

  /**
   * Search the index for entries matching the query string.
   * @param userQuery the string to search for
   * @param max the maximum number of results to return
   * @return a set of matching Entry objects
   * @throws IOException if there is a low-level IO error
   */
  public List<Entry> search(String userQuery, int max) throws IOException {
    Query query = createRootsQuery(userQuery);
    SortField sortField = new SortField(Lucene.SORT, SortField.STRING);
    Sort sort = new Sort(sortField);
    List<Document> documents = docsFromQuery(query, sort, max);
    if (documents.isEmpty()) {
      query = createFormsQuery(userQuery);
      documents = docsFromQuery(query, sort, max);
      if (documents.isEmpty()) {
        query = createFullTextQuery(userQuery);
        documents = docsFromQuery(query, sort, max);
      }
    }
    List<Entry> entryList = new ArrayList<>();
    for (Document document : documents) {
      Entry entry = documentToEntry(document);
      entryList.add(entry);
    }
    return entryList;
  }

  /**
   * Search the index for entries matching the query string.
   * @param userQuery the string to search for
   * @param language the source language
   * @param max the maximum number of results to return
   * @return a set of matching Entry objects
   * @throws IOException if there is a low-level IO error
   */
  public List<Entry> search(String userQuery, Language language, int max) throws IOException {
    Query query = createRootsQuery(userQuery, language);
    SortField sortField = new SortField(Lucene.SORT, SortField.STRING);
    Sort sort = new Sort(sortField);
    List<Document> documents = docsFromQuery(query, sort, max);
    if (documents.isEmpty()) {
      query = createFormsQuery(userQuery, language);
      documents = docsFromQuery(query, sort, max);
      if (documents.isEmpty()) {
        query = createFullTextQuery(userQuery, language);
        documents = docsFromQuery(query, sort, max);
      }
    }
    List<Entry> entryList = new ArrayList<>();
    for (Document document : documents) {
      Entry entry = documentToEntry(document);
      entryList.add(entry);
    }
    return entryList;
  }

  private Query createRootsQuery(String userQuery) throws IOException {
    String[] fields = new String[] {Lucene.HU_ROOTS, Lucene.NO_ROOTS};
    return createQueryFromFields(userQuery, fields, false);
  }

  private Query createRootsQuery(String userQuery, Language language) throws IOException {
    String[] fields;
    if (Language.HU.equals(language)) {
      fields = new String[] {Lucene.HU_ROOTS};
    } else {
      fields = new String[] {Lucene.NO_ROOTS};
    }
    return createQueryFromFields(userQuery, fields, false);
  }

  private Query createFormsQuery(String userQuery) throws IOException {
    String[] fields = new String[] {Lucene.HU_FORMS, Lucene.NO_FORMS};
    return createQueryFromFields(userQuery, fields, false);
  }

  private Query createFormsQuery(String userQuery, Language language) throws IOException {
    String[] fields;
    if (Language.HU.equals(language)) {
      fields = new String[] {Lucene.HU_FORMS};
    } else {
      fields = new String[] {Lucene.NO_FORMS};
    }
    return createQueryFromFields(userQuery, fields, false);
  }

  private Query createFullTextQuery(String userQuery) throws IOException {
    String[] fields = new String[] {Lucene.NO_TRANS, Lucene.HU_QUOTE, Lucene.NO_QUOTETRANS,
        Lucene.HU_TRANS, Lucene.NO_QUOTE, Lucene.HU_QUOTETRANS};
    return createQueryFromFields(userQuery, fields, false);
  }

  private Query createFullTextQuery(String userQuery, Language language) throws IOException {
    String[] fields;
    if (Language.HU.equals(language)) {
      fields = new String[] {Lucene.NO_TRANS, Lucene.HU_QUOTE, Lucene.NO_QUOTETRANS};
    } else {
      fields = new String[] {Lucene.HU_TRANS, Lucene.NO_QUOTE, Lucene.HU_QUOTETRANS};
    }
    return createQueryFromFields(userQuery, fields, false);
  }

  private Query createQueryFromFields(String userQuery, String[] fields, boolean wildcards)
      throws IOException {
    BooleanQuery luceneQuery = new BooleanQuery();
    for (String field : fields) {
      BooleanQuery fieldQuery = new BooleanQuery();
      List<String> tokens = extractTokens(userQuery, field);
      for (String token : tokens) {
        if (wildcards) {
          WildcardQuery wildcardQuery = new WildcardQuery(
              new Term(field, token + "*"));
          fieldQuery.add(new BooleanClause(wildcardQuery, Occur.MUST));
        } else {
          TermQuery termQuery = new TermQuery(
              new Term(field, token));
          fieldQuery.add(new BooleanClause(termQuery, Occur.MUST));
        }
      }
      luceneQuery.add(new BooleanClause(fieldQuery, Occur.SHOULD));
    }
    return luceneQuery;
  }

  private List<String> extractTokens(String query, String field) throws IOException {
    List<String> tokens = new ArrayList<>();
    Reader reader = new StringReader(query);
    TokenStream tokenStream = analyzer.tokenStream(field, reader);
    CharTermAttribute attribute = tokenStream.getAttribute(CharTermAttribute.class);
    while (tokenStream.incrementToken()) {
      tokens.add(attribute.toString());
    }
    return tokens;
  }

  private List<Document> docsFromQuery(Query query, Sort sort, int max) throws IOException {
    List<Document> results = new ArrayList<>();
    IndexSearcher indexSearcher = new IndexSearcher(indexReader);
    TopDocs topDocs = executeSearch(indexSearcher, query, max, sort);
    ScoreDoc[] scoreDocs = topDocs.scoreDocs;
    for (ScoreDoc scoreDoc: scoreDocs) {
      Document document = extractDocument(scoreDoc);
      results.add(document);
    }
    return results;
  }

  private Entry documentToEntry(Document document) {
    Entry entry = new Entry();
    entry.setId(document.get(Lucene.ID));
    entry.setLang(Language.valueOf(document.get(Lucene.LANG).toUpperCase(Locale.getDefault())));
    entry.setText(document.get(Lucene.TEXT));
    return entry;
  }

  private String[] executeSuggestion(String query, int maxSuggestions) throws IOException {
    return spellChecker.suggestSimilar(query, maxSuggestions);
  }

  private TopDocs executeSearch(IndexSearcher indexSearcher, Query query,
      int maxSuggestions, Sort sort) throws IOException {
    return indexSearcher.search(query, maxSuggestions, sort);
  }

  private Document extractDocument(ScoreDoc scoreDoc) throws IOException {
    return indexReader.document(scoreDoc.doc);
  }

}
