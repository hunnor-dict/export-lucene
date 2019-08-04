package net.hunnor.dict.lucene.searcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.hunnor.dict.lucene.model.Entry;
import net.hunnor.dict.lucene.model.Language;
import net.hunnor.dict.lucene.searcher.LuceneSearcher;

import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.NIOFSDirectory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class LuceneSearcherTest {

  private LuceneSearcher searcher;

  /**
   * Initialize the searcher before each text.
   *
   * @throws IOException if there is a low-level IO error
   */
  @BeforeEach
  public void setUp() throws IOException {
    searcher = LuceneSearcher.getInstance();
    searcher.open(new File(
        getClass().getResource("/lucene-index").getFile()));
    searcher.openSpellChecker(new File(
        getClass().getResource("/lucene-spellchecker-index").getFile()));
  }

  @AfterEach
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
  public void testOpenSpellCheckerNewDirectory(@TempDir File tempDir) throws IOException {
    searcher.closeSpellChecker();
    File directory = new File(tempDir, "tempDir");
    boolean createDirectory = directory.mkdir();
    assertTrue(createDirectory);
    assertTrue(directory.isDirectory());
    File[] files = directory.listFiles();
    assertEquals(0, files.length);
    searcher.openSpellChecker(directory);
    assertFalse(searcher.isSpellCheckerOpen());
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
    List<Entry> results = searcher.search("aaaaaa", 100);
    assertEquals(1, results.size());
  }

  @Test
  public void testSearchForForms() {
    List<Entry> results = searcher.search("bbbbbb", 100);
    assertEquals(2, results.size());
  }

  @Test
  public void testSearchForQuotes() {
    List<Entry> results = searcher.search("cccccc", 100);
    assertEquals(2, results.size());
  }

  @Test
  public void testSearchForRootsLang() {
    List<Entry> results = searcher.search("aaaaaa", Language.HU, 100);
    assertEquals(1, results.size());
    results = searcher.search("aaaaab", Language.NO, 100);
    assertEquals(1, results.size());
  }

  @Test
  public void testSearchForFormsLang() {
    List<Entry> results = searcher.search("bbbbbb", Language.HU, 100);
    assertEquals(1, results.size());
    results = searcher.search("bbbbbb", Language.NO, 100);
    assertEquals(1, results.size());
  }

  @Test
  public void testSearchForQuotesLang() {
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
