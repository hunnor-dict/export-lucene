package net.hunnor.dict.lucene.searcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import net.hunnor.dict.lucene.model.Entry;
import net.hunnor.dict.lucene.model.Language;
import net.hunnor.dict.lucene.searcher.LuceneSearcher;

import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.NIOFSDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class LuceneSearcherTest {

  private LuceneSearcher searcher;

  /**
   * Initialize the searcher before each text.
   *
   * @throws IOException if there is a low-level IO error
   */
  @Before
  public void setUp() throws IOException {
    searcher = LuceneSearcher.getInstance();
    searcher.open(new File(
        getClass().getResource("/lucene-index").getFile()));
    searcher.openSpellChecker(new File(
        getClass().getResource("/lucene-spellchecker-index").getFile()));
  }

  @After
  public void tearDown() throws IOException {
    searcher.close();
    searcher.closeSpellChecker();
  }

  @Test
  public void testClose() throws IOException {
    searcher.close();
    assertFalse(searcher.isOpen());
    searcher.close();
    assertFalse(searcher.isOpen());
  }

  @Test
  public void testCloseSpellChecker() throws IOException {
    searcher.closeSpellChecker();
    assertFalse(searcher.isSpellCheckerOpen());
    searcher.closeSpellChecker();
    assertFalse(searcher.isSpellCheckerOpen());
  }

  @Test
  public void testOpen() throws IOException {
    assertTrue(searcher.isOpen());
  }

  @Test
  public void testOpenSpellChecker() throws IOException {
    assertTrue(searcher.isSpellCheckerOpen());
  }

  @Test
  public void testSuggestion() {
    List<String> suggestions = searcher.suggestions("aaa", 20);
    assertNotNull(suggestions);
    assertEquals(3, suggestions.size());
    assertEquals("aaaaaa", suggestions.get(0));
    assertEquals("aaaaab", suggestions.get(1));
    assertEquals("aaaaac", suggestions.get(2));
  }

  @Test
  public void testSpellingSuggestions() {
    List<String> suggestions = searcher.spellingSuggestions("aabaaa", 5);
    assertNotNull(suggestions);
    assertEquals(3, suggestions.size());
    assertEquals("aaaaaa", suggestions.get(0));
  }

  @Test
  public void testSpellingSuggestionsError() throws Exception {
    SpellChecker spySpellChecker = spy(new SpellChecker(new NIOFSDirectory(new File(
        getClass().getResource("/lucene-spellchecker-index").getFile()))));
    doThrow(new IOException()).when(spySpellChecker).suggestSimilar("aaa", 20);
    searcher.setSpellChecker(spySpellChecker);
    List<String> suggestions = searcher.spellingSuggestions("aaa", 20);
    assertEquals(0, suggestions.size());
  }

  @Test
  public void testSearchForRoots() {
    List<Entry> results = searcher.search("aaaaaa", Language.HU, 100);
    assertEquals(1, results.size());
    results = searcher.search("aaaaab", Language.NO, 100);
  }

  @Test
  public void testSearchForForms() {
    List<Entry> results = searcher.search("bbbbbb", Language.HU, 100);
    assertEquals(1, results.size());
    results = searcher.search("bbbbbb", Language.NO, 100);
    assertEquals(1, results.size());
  }

  @Test
  public void testSearchForQuotes() {
    List<Entry> results = searcher.search("cccccc", Language.HU, 100);
    assertEquals(1, results.size());
    results = searcher.search("cccccc", Language.NO, 100);
    assertEquals(1, results.size());
  }

  @Test
  public void testNoResults() {
    List<Entry> results = searcher.search("ffffff", Language.HU, 100);
    assertEquals(0, results.size());
  }

}
