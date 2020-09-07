package net.hunnor.dict.lucene.indexer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import net.hunnor.dict.lucene.model.Entry;
import net.hunnor.dict.lucene.model.Language;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LuceneIndexerTest {

  private static final String INDEX_DIR = "index";

  private static final String SPELLING_DIR = "spelling";

  @Test
  void closeWhenAlreadyClosed() throws IOException {
    LuceneIndexer indexer = new LuceneIndexer();
    assertNull(indexer.getIndexDir());
    indexer.closeIndexReader();
    indexer.closeIndexWriter();
    assertNull(indexer.getSpellingDir());
    indexer.closeSpellChecker();
  }

  @Test
  void writeEntryToClosedWriter() throws IOException {
    LuceneIndexer indexer = new LuceneIndexer();
    assertNull(indexer.getIndexDir());
    Entry entry = new Entry();
    indexer.write(entry);
  }

  @Test
  void testIndexing(@TempDir File tempDir) throws IOException {

    LuceneIndexer indexer = new LuceneIndexer();

    File indexDir = new File(tempDir, INDEX_DIR);
    File spellingDir = new File(tempDir, SPELLING_DIR);

    indexer.setIndexDir(indexDir.getAbsolutePath());
    indexer.setSpellingDir(spellingDir.getAbsolutePath());

    assertEquals(indexDir.getAbsolutePath(), indexer.getIndexDir());
    assertEquals(spellingDir.getAbsolutePath(), indexer.getSpellingDir());

    Entry entry1 = new Entry();
    entry1.setLang(Language.HU);
    entry1.setId("1");
    entry1.setRoots(new HashSet<String>(Arrays.asList(new String[] {"aaaaaa", "aaaaab"})));
    entry1.setForms(new HashSet<String>(Arrays.asList(new String[] {"bbbbbb"})));
    entry1.setQuote(new HashSet<String>(Arrays.asList(new String[] {"cccccc"})));
    entry1.setTrans(new HashSet<String>(Arrays.asList(new String[] {"dddddd"})));
    entry1.setQuoteTrans(new HashSet<String>(Arrays.asList(new String[] {"eeeeee"})));
    entry1.setText("ffffff");
    Entry entry2 = new Entry();
    entry2.setLang(Language.NO);
    entry2.setId("2");
    entry2.setRoots(new HashSet<String>(Arrays.asList(new String[] {"aaaaab", "aaaaac"})));
    entry2.setSort("aaaaab");
    entry2.setForms(new HashSet<String>(Arrays.asList(new String[] {"bbbbbb"})));
    entry2.setQuote(new HashSet<String>(Arrays.asList(new String[] {"cccccc"})));
    entry2.setTrans(new HashSet<String>(Arrays.asList(new String[] {"dddddd"})));
    entry2.setQuoteTrans(new HashSet<String>(Arrays.asList(new String[] {"eeeeee"})));
    entry2.setText("ffffff");

    indexer.openIndexWriter();
    indexer.write(entry1);
    indexer.write(entry2);
    indexer.closeIndexWriter();

    indexer.openIndexReader();
    indexer.createSuggestions();
    indexer.openSpellChecker();
    indexer.createSuggestions();
    indexer.closeSpellChecker();
    indexer.closeIndexReader();

    assertTrue(indexDir.list().length > 0);
    assertTrue(spellingDir.list().length > 0);

  }

}
