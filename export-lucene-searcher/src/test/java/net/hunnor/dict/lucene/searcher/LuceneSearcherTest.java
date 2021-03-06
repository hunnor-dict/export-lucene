package net.hunnor.dict.lucene.searcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import net.hunnor.dict.lucene.model.Entry;
import net.hunnor.dict.lucene.model.Language;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LuceneSearcherTest {

  private LuceneSearcher searcher;

  /**
   * Initialize the searcher before each text.
   *
   * @throws IOException if there is a low-level IO error
   */
  @BeforeEach
  void setUp() throws IOException {
    searcher = LuceneSearcher.getInstance();
    searcher.open(new File(
        getClass().getResource("/3_6_2/lucene-index").getFile()));
    searcher.openSpellChecker(new File(
        getClass().getResource("/3_6_2/lucene-spellchecker-index").getFile()));
  }

  @AfterEach
  void tearDown() throws IOException {
    searcher.close();
    searcher.closeSpellChecker();
  }

  @Test
  void testClose() throws IOException {
    searcher.close();
    assertFalse(searcher.isOpen());
    searcher.close();
    assertFalse(searcher.isOpen());
  }

  @Test
  void testCloseSpellChecker() throws IOException {
    searcher.closeSpellChecker();
    assertFalse(searcher.isSpellCheckerOpen());
    searcher.closeSpellChecker();
    assertFalse(searcher.isSpellCheckerOpen());
  }

  @Test
  void testOpen() throws IOException {
    assertTrue(searcher.isOpen());
  }

  @Test
  void testOpenSpellChecker() throws IOException {
    assertTrue(searcher.isSpellCheckerOpen());
  }

  @Test
  void testOpenSpellCheckerNewDirectory(@TempDir File tempDir) throws IOException {
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
  void testOpenSpellCheckerFile(@TempDir File tempDir) throws IOException {
    searcher.closeSpellChecker();
    File file = new File(tempDir, "tempFile");
    boolean createFile = file.createNewFile();
    assertTrue(createFile);
    assertTrue(file.isFile());
    searcher.openSpellChecker(file);
    assertFalse(searcher.isSpellCheckerOpen());
  }

  @Test
  void testOpenSpellcheckerUnreadable(@TempDir File tempDir) throws IOException {
    searcher.closeSpellChecker();
    File file = new File(tempDir, "tempFile");
    boolean createFile = file.createNewFile();
    assertTrue(createFile);
    assertTrue(file.isFile());
    file.setReadable(false);
    searcher.openSpellChecker(file);
    assertFalse(searcher.isSpellCheckerOpen());
  }

  @Test
  void testSuggestion() throws IOException {
    List<String> suggestions = searcher.suggestions("aaa", 20);
    assertNotNull(suggestions);
    assertEquals(3, suggestions.size());
    assertEquals("aaaaaa", suggestions.get(0));
    assertEquals("aaaaab", suggestions.get(1));
    assertEquals("aaaaac", suggestions.get(2));
  }

  @Test
  void testSpellingSuggestions() throws IOException {
    List<String> suggestions = searcher.spellingSuggestions("aabaaa", 5);
    assertNotNull(suggestions);
    assertEquals(3, suggestions.size());
    assertEquals("aaaaaa", suggestions.get(0));
  }

  @Test
  void testSearchForRoots() throws IOException {
    List<Entry> results = searcher.search("aaaaaa", 100);
    assertEquals(1, results.size());
  }

  @Test
  void testSearchForForms() throws IOException {
    List<Entry> results = searcher.search("bbbbbb", 100);
    assertEquals(2, results.size());
  }

  @Test
  void testSearchForQuotes() throws IOException {
    List<Entry> results = searcher.search("cccccc", 100);
    assertEquals(2, results.size());
  }

  @Test
  void testSearchForRootsLang() throws IOException {
    List<Entry> results = searcher.search("aaaaaa", Language.HU, 100);
    assertEquals(1, results.size());
    results = searcher.search("aaaaab", Language.NO, 100);
    assertEquals(1, results.size());
  }

  @Test
  void testSearchForFormsLang() throws IOException {
    List<Entry> results = searcher.search("bbbbbb", Language.HU, 100);
    assertEquals(1, results.size());
    results = searcher.search("bbbbbb", Language.NO, 100);
    assertEquals(1, results.size());
  }

  @Test
  void testSearchForQuotesLang() throws IOException {
    List<Entry> results = searcher.search("cccccc", Language.HU, 100);
    assertEquals(1, results.size());
    results = searcher.search("cccccc", Language.NO, 100);
    assertEquals(1, results.size());
  }

  @Test
  void testNoResults() throws IOException {
    List<Entry> results = searcher.search("ffffff", Language.HU, 100);
    assertEquals(0, results.size());
  }

  @Test
  void testAsciiFolding() throws IOException {
    List<Entry> results = searcher.search("tttto", Language.HU, 100);
    assertEquals(2, results.size());
    results = searcher.search("ttttó", Language.HU, 100);
    assertEquals(2, results.size());
    results = searcher.search("ttttő", Language.HU, 100);
    assertEquals(2, results.size());
    results = searcher.search("tttto", Language.NO, 100);
    assertEquals(2, results.size());
    results = searcher.search("ttttø", Language.NO, 100);
    assertEquals(2, results.size());
  }

  @Test
  void testSpellingSuggestionsSpecials() throws IOException {
    List<String> suggestions = searcher.spellingSuggestions("ttttt", 5);
    assertNotNull(suggestions);
    assertEquals(4, suggestions.size());
    assertTrue(suggestions.contains("ttttó"));
    assertTrue(suggestions.contains("ttttő"));
    assertTrue(suggestions.contains("tttto"));
    assertTrue(suggestions.contains("ttttø"));
  }

}
