package net.hunnor.dict.lucene.searcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import net.hunnor.dict.lucene.model.Entry;
import net.hunnor.dict.lucene.model.Language;
import net.hunnor.dict.lucene.searcher.LuceneSearcher;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.management.*")
@PrepareForTest(LuceneSearcher.class)
public class LuceneSearcherTest {

  private LuceneSearcher searcher;

  /**
   * Initialize the searcher before each text.

   * @throws IOException if an error occurs
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
  public void testSuggestionError() throws Exception {
    LuceneSearcher spySearcher = PowerMockito.spy(searcher);
    PowerMockito.doThrow(new IOException()).when(spySearcher, "executeSearch",
        Matchers.any(), Matchers.any(),
        Matchers.anyInt(), Matchers.any());
    List<String> suggestions = spySearcher.suggestions("aaa", 20);
    assertEquals(0, suggestions.size());
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
    LuceneSearcher spySearcher = PowerMockito.spy(searcher);
    PowerMockito.doThrow(new IOException()).when(spySearcher, "executeSuggestion",
        Matchers.any(), Matchers.anyInt());
    List<String> suggestions = spySearcher.spellingSuggestions("aabaaa", 5);
    assertNotNull(suggestions);
    assertEquals(0, suggestions.size());
  }

  @Test
  public void testSearchForRoots() {
    List<Entry> results = searcher.search("aaaaaa", Language.HU, 100);
    assertEquals(1, results.size());
    results = searcher.search("aaaaab", Language.NO, 100);
  }

  @Test
  public void testSearchForRootsSearchError() throws Exception {
    LuceneSearcher spySearcher = PowerMockito.spy(searcher);
    PowerMockito.doThrow(new IOException()).when(spySearcher, "executeSearch",
        Matchers.any(), Matchers.any(),
        Matchers.anyInt(), Matchers.any());
    List<Entry> results = spySearcher.search("aaaaaa", Language.HU, 100);
    assertNotNull(results);
    assertEquals(0, results.size());
  }

  @Test
  public void testSearchForRootsTokenError() throws Exception {
    LuceneSearcher spySearcher = PowerMockito.spy(searcher);
    PowerMockito.doThrow(new IOException()).when(spySearcher, "extractTokens",
        Matchers.any(), Matchers.any());
    List<Entry> results = spySearcher.search("aaaaaa", Language.HU, 100);
    assertNotNull(results);
    assertEquals(0, results.size());
  }

  @Test
  public void testSearchForRootsDocumentError() throws Exception {
    LuceneSearcher spySearcher = PowerMockito.spy(searcher);
    PowerMockito.doThrow(new IOException()).when(spySearcher, "extractDocument",
        Matchers.any());
    List<Entry> results = spySearcher.search("aaaaaa", Language.HU, 100);
    assertNotNull(results);
    assertEquals(0, results.size());
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
