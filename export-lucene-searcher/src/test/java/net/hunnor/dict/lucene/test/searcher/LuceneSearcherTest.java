package net.hunnor.dict.lucene.test.searcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import net.hunnor.dict.lucene.indexer.LuceneIndexer;
import net.hunnor.dict.lucene.model.Entry;
import net.hunnor.dict.lucene.searcher.LuceneSearcher;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class LuceneSearcherTest {

  private static final String INDEX_DIR = "hunnor-lucene-index";

  private static final String SPELLING_DIR = "hunnor-lucene-spelling";

  private static TemporaryFolder testFolder = new TemporaryFolder();

  private LuceneSearcher searcher;

  /**
   * Create the index for the tests.
   * 
   * @throws IOException if an error occurs
   */
  @BeforeClass
  public static void setUpIndex() throws IOException {

    testFolder.create();

    LuceneIndexer indexer = new LuceneIndexer();
    File indexDir = testFolder.newFolder(INDEX_DIR);
    indexer.setIndexDir(indexDir.getAbsolutePath());
    File spellingDir = testFolder.newFolder(SPELLING_DIR);
    indexer.setSpellingDir(spellingDir.getAbsolutePath());

    indexer.openIndexWriter();

    Entry entry = new Entry();
    entry.setLang("hu");
    entry.setId("1");
    entry.setRoots(new HashSet<String>(Arrays.asList(new String[] {"aaaaaa", "aaaaab"})));
    entry.setForms(new HashSet<String>(Arrays.asList(new String[] {"bbbbbb"})));
    indexer.write(entry);
    
    entry = new Entry();
    entry.setLang("no");
    entry.setId("2");
    entry.setRoots(new HashSet<String>(Arrays.asList(new String[] {"aaaaab", "aaaaac"})));
    indexer.write(entry);

    indexer.closeIndexWriter();

    indexer.openIndexReader();
    indexer.openSpellChecker();
    indexer.createSuggestions();
    indexer.closeSpellChecker();
    indexer.closeIndexReader();

  }

  /**
   * Initialize the searcher before each text.

   * @throws IOException if an error occurs
   */
  @Before
  public void setUpSearcher() throws IOException {
    searcher = new LuceneSearcher();
    searcher.open(
        new File(testFolder.getRoot(), INDEX_DIR), new File(testFolder.getRoot(), SPELLING_DIR));
  }

  @Test
  public void testSuggestion() {
    List<String> suggestions = searcher.suggestions("aaa");
    assertNotNull(suggestions);
    assertEquals(3, suggestions.size());
    assertEquals("aaaaaa", suggestions.get(0));
    assertEquals("aaaaab", suggestions.get(1));
    assertEquals("aaaaac", suggestions.get(2));
  }

  @Test
  public void testSpellingSuggestions() {
    List<String> suggestions = searcher.spellingSuggestions("aabaaa");
    assertNotNull(suggestions);
    assertEquals(3, suggestions.size());
    assertEquals("aaaaaa", suggestions.get(0));
  }

  @Test
  public void testSearchForRoots() {
    List<Entry> results = searcher.search("aaaaaa", LuceneSearcher.LANG_HU);
    assertEquals(1, results.size());
  }

  @Test
  public void testSearchForForms() {
    List<Entry> results = searcher.search("bbbbbb", LuceneSearcher.LANG_HU);
    assertEquals(1, results.size());
  }

}