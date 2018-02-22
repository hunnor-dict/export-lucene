package net.hunnor.dict.lucene.searcher;

import net.hunnor.dict.lucene.analyzer.PerFieldAnalyzer;
import net.hunnor.dict.lucene.constants.Lucene;
import net.hunnor.dict.lucene.model.Entry;
import net.hunnor.dict.lucene.model.Language;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LuceneSearcher {

  public static final int MAX_RESULTS = 1000;

  public static final int MAX_SUGGESTION_DOCS = 20;

  public static final int MAX_SPELLING_SUGGESTIONS = 5;

  private static final Logger LOGGER = LoggerFactory.getLogger(LuceneSearcher.class);

  private static volatile LuceneSearcher instance;

  private static final Object MUTEX = new Object();

  private IndexReader indexReader;

  private SpellChecker spellChecker;

  private Analyzer analyzer = PerFieldAnalyzer.getInstance(Lucene.VERSION);

  /**
   * Get the single instance of the class.
   *
   * @return the single instance of the class
   */
  public static LuceneSearcher getInstance() {
    LuceneSearcher result = instance;
    if (result == null) {
      synchronized (MUTEX) {
        result = instance;
        if (result == null) {
          instance = result = new LuceneSearcher();
        }
      }
    }
    return result;
  }

  public boolean isOpen() {
    return indexReader != null;
  }

  public boolean isSpellCheckerOpen() {
    return spellChecker != null;
  }

  public void open(File indexDirectory) throws IOException {
    indexReader = IndexReader.open(new NIOFSDirectory(indexDirectory));
  }

  public void openSpellChecker(File spellingDirectory) throws IOException {
    spellChecker = new SpellChecker(new NIOFSDirectory(spellingDirectory));
  }

  /**
   * Close the index reader.
   *
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
   *
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
   *
   * @param userQuery the user query to return suggestions for
   * @return a set of matching terms
   */
  public List<String> suggestions(String userQuery) {

    Query query = createQueryFromFields(userQuery, new String[] {Lucene.SUGGESTION}, true);
    SortField sortField = new SortField(Lucene.SUGGESTION, SortField.STRING);
    Sort sort = new Sort(sortField);

    TopDocs topDocs = null;
    try (IndexSearcher indexSearcher = new IndexSearcher(indexReader)) {
      topDocs = executeSearch(indexSearcher, query, MAX_SUGGESTION_DOCS, sort);
    } catch (IOException ex) {
      LOGGER.error(ex.getMessage(), ex);
    }

    List<String> results = new ArrayList<>();

    if (topDocs != null) {
      ScoreDoc[] scoreDocs = topDocs.scoreDocs;
      results = Arrays.stream(scoreDocs)
          .map(this::scoreDocToDocument)
          .filter(Objects::nonNull)
          .map(document -> document.get(Lucene.SUGGESTION))
          .distinct()
          .collect(Collectors.toList());
    }

    return results;

  }

  /**
   * Suggest terms similar to the user query, using Lucene's spell checker.
   *
   * @param userQuery the user query to return suggestions for
   * @return a set of terms returned by the spell checker
   */
  public List<String> spellingSuggestions(String userQuery) {
    List<String> results = new ArrayList<>();
    try {
      String[] suggestions = executeSuggestion(userQuery, MAX_SPELLING_SUGGESTIONS);
      results.addAll(Arrays.asList(suggestions));
    } catch (IOException ex) {
      LOGGER.error(ex.getMessage(), ex);
    }
    return results;
  }

  /**
   * Search the index for entries matching the query string.
   *
   * @param userQuery the string to search for
   * @param language the source language
   * @return a set of matching Entry objects
   */
  public List<Entry> search(String userQuery, Language language) {
    Query query = createRootsQuery(userQuery, language);
    List<Entry> results = executeQuery(query);
    if (results.isEmpty()) {
      query = createFormsQuery(userQuery, language);
      results = executeQuery(query);
      if (results.isEmpty()) {
        query = createFullTextQuery(userQuery, language);
        results = executeQuery(query);
      }
    }
    return results;
  }

  private TopDocs executeSearch(IndexSearcher indexSearcher, Query query,
      int maxSuggestions, Sort sort) throws IOException {
    if (sort == null) {
      return indexSearcher.search(query, maxSuggestions);
    } else {
      return indexSearcher.search(query, maxSuggestions, sort);
    }
  }

  private String[] executeSuggestion(String query, int maxSuggestions) throws IOException {
    return spellChecker.suggestSimilar(query, maxSuggestions);
  }

  private Query createRootsQuery(String userQuery, Language language) {
    String[] fields;
    if (Language.hu.equals(language)) {
      fields = new String[] {Lucene.HU_ROOTS};
    } else {
      fields = new String[] {Lucene.NO_ROOTS};
    }
    return createQueryFromFields(userQuery, fields, false);
  }

  private Query createFormsQuery(String userQuery, Language language) {
    String[] fields;
    if (Language.hu.equals(language)) {
      fields = new String[] {Lucene.HU_FORMS};
    } else {
      fields = new String[] {Lucene.NO_FORMS};
    }
    return createQueryFromFields(userQuery, fields, false);
  }

  private Query createFullTextQuery(String userQuery, Language language) {
    String[] fields;
    if (Language.hu.equals(language)) {
      fields = new String[] {Lucene.NO_TRANS, Lucene.HU_QUOTE, Lucene.NO_QUOTETRANS};
    } else {
      fields = new String[] {Lucene.HU_TRANS, Lucene.NO_QUOTE, Lucene.HU_QUOTETRANS};
    }
    return createQueryFromFields(userQuery, fields, false);
  }

  private Query createQueryFromFields(String userQuery, String[] fields, boolean wildcards) {
    BooleanQuery luceneQuery = new BooleanQuery();
    Arrays.stream(fields).forEach(field -> {
      BooleanQuery fieldQuery = new BooleanQuery();
      try {
        List<String> tokens = extractTokens(userQuery, field);
        tokens.forEach(token -> {
          if (wildcards) {
            WildcardQuery wildcardQuery = new WildcardQuery(
                new Term(field, token + "*"));
            fieldQuery.add(new BooleanClause(wildcardQuery, Occur.MUST));
          } else {
            TermQuery termQuery = new TermQuery(
                new Term(field, token));
            fieldQuery.add(new BooleanClause(termQuery, Occur.MUST));
          }
        });
      } catch (IOException ex) {
        LOGGER.error(ex.getMessage(), ex);
      }
      luceneQuery.add(new BooleanClause(fieldQuery, Occur.SHOULD));
    });
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

  private List<Entry> executeQuery(Query query) {
    List<Entry> results = new ArrayList<>();
    try (IndexSearcher indexSearcher = new IndexSearcher(indexReader)) {
      TopDocs topDocs = executeSearch(indexSearcher, query, MAX_RESULTS, null);
      ScoreDoc[] scoreDocs = topDocs.scoreDocs;
      results = Arrays.stream(scoreDocs)
          .map(this::scoreDocToDocument)
          .filter(Objects::nonNull)
          .map(this::documentToEntry)
          .collect(Collectors.toList());
    } catch (IOException ex) {
      LOGGER.error(ex.getMessage(), ex);
    }
    return results;
  }

  private Document scoreDocToDocument(ScoreDoc scoreDoc) {
    Document document = null;
    try {
      document = extractDocument(scoreDoc);
    } catch (IOException ex) {
      LOGGER.error(ex.getMessage(), ex);
    }
    return document;
  }

  private Document extractDocument(ScoreDoc scoreDoc) throws CorruptIndexException, IOException {
    return indexReader.document(scoreDoc.doc);
  }

  private Entry documentToEntry(Document document) {
    Entry entry = new Entry();
    entry.setId(document.get(Lucene.ID));
    entry.setLang(Language.valueOf(document.get(Lucene.LANG)));
    entry.setText(document.get(Lucene.TEXT));
    return entry;
  }

}
