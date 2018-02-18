package net.hunnor.dict.lucene.searcher;

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
    return analyzer != null && indexReader != null && spellChecker != null;
  }

  public void open(File indexDirectory, File spellingDirectory) throws IOException {
    indexReader = IndexReader.open(new NIOFSDirectory(indexDirectory));
    spellChecker = new SpellChecker(new NIOFSDirectory(spellingDirectory));
  }

  /**
   * Close the index reader and the spell checker.
   *
   * @throws IOException if an error occurs while closing an index
   */
  public void close() throws IOException {
    if (indexReader != null) {
      indexReader.close();
      indexReader = null;
    }
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
    List<String> results = new ArrayList<>();
    try (IndexSearcher indexSearcher = new IndexSearcher(indexReader)) {
      Query query = createQueryFromFields(userQuery, new String[] {Lucene.SUGGESTION}, true);

      SortField sortField = new SortField(Lucene.SUGGESTION, SortField.STRING);
      Sort sort = new Sort(sortField);

      TopDocs topDocs = indexSearcher.search(query, MAX_SUGGESTION_DOCS, sort);
      ScoreDoc[] scoreDocs = topDocs.scoreDocs;

      Arrays.stream(scoreDocs)
          .map(this::scoreDocToDocument)
          .filter(Objects::nonNull)
          .map(document -> document.get(Lucene.SUGGESTION))
          .distinct()
          .forEach(results::add);

    } catch (IOException ex) {
      LOGGER.error(ex.getMessage(), ex);
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
      String[] suggestions = spellChecker.suggestSimilar(userQuery, MAX_SPELLING_SUGGESTIONS);
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

  private Query createRootsQuery(String userQuery, Language language) {
    String[] fields = new String[] {};
    if (Language.hu.equals(language)) {
      fields = new String[] {Lucene.HU_ROOTS};
    } else if (Language.no.equals(language)) {
      fields = new String[] {Lucene.NO_ROOTS};
    }
    return createQueryFromFields(userQuery, fields, false);
  }

  private Query createFormsQuery(String userQuery, Language language) {
    String[] fields = new String[] {};
    if (Language.hu.equals(language)) {
      fields = new String[] {Lucene.HU_FORMS};
    } else if (Language.no.equals(language)) {
      fields = new String[] {Lucene.NO_FORMS};
    }
    return createQueryFromFields(userQuery, fields, false);
  }

  private Query createFullTextQuery(String userQuery, Language language) {
    String[] fields = new String[] {};
    if (Language.hu.equals(language)) {
      fields = new String[] {Lucene.NO_TRANS, Lucene.HU_QUOTE, Lucene.NO_QUOTETRANS};
    } else if (Language.no.equals(language)) {
      fields = new String[] {Lucene.HU_TRANS, Lucene.NO_QUOTE, Lucene.HU_QUOTETRANS};
    }
    return createQueryFromFields(userQuery, fields, false);
  }

  private Query createQueryFromFields(String userQuery, String[] fields, boolean wildcards) {
    BooleanQuery luceneQuery = new BooleanQuery();
    Arrays.stream(fields).forEach(field -> {
      try {
        BooleanQuery fieldQuery = new BooleanQuery();
        Reader reader = new StringReader(userQuery);
        TokenStream tokenStream = analyzer.tokenStream(field, reader);
        CharTermAttribute attribute = tokenStream.getAttribute(CharTermAttribute.class);
        while (tokenStream.incrementToken()) {
          if (wildcards) {
            WildcardQuery wildcardQuery = new WildcardQuery(
                new Term(field, attribute.toString() + "*"));
            fieldQuery.add(new BooleanClause(wildcardQuery, Occur.MUST));
          } else {
            TermQuery termQuery = new TermQuery(
                new Term(field, attribute.toString()));
            fieldQuery.add(new BooleanClause(termQuery, Occur.MUST));
          }
        }
        luceneQuery.add(new BooleanClause(fieldQuery, Occur.SHOULD));
      } catch (IOException ex) {
        LOGGER.error(ex.getMessage(), ex);
      }
    });
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Query for '{}' with fields {}: {}", userQuery, fields, luceneQuery);
    }
    return luceneQuery;
  }

  private List<Entry> executeQuery(Query query) {
    List<Entry> results = new ArrayList<>();
    try (IndexSearcher indexSearcher = new IndexSearcher(indexReader)) {
      TopDocs topDocs = indexSearcher.search(query, MAX_RESULTS);
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
      document = indexReader.document(scoreDoc.doc);
    } catch (IOException ex) {
      LOGGER.error(ex.getMessage(), ex);
    }
    return document;
  }

  private Entry documentToEntry(Document document) {
    Entry entry = new Entry();
    entry.setId(document.get(Lucene.ID));
    entry.setLang(Language.valueOf(document.get(Lucene.LANG)));
    entry.setText(document.get(Lucene.TEXT));
    return entry;
  }

}
