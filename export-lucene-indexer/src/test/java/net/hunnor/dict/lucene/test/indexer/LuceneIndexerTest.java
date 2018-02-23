package net.hunnor.dict.lucene.test.indexer;

import net.hunnor.dict.lucene.indexer.LuceneIndexer;
import net.hunnor.dict.lucene.model.Entry;
import net.hunnor.dict.lucene.model.Language;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

public class LuceneIndexerTest {

  private static final String INDEX_DIR = "indexDir";

  private static final String SPELLING_DIR = "spellingDir";

  @Rule public TemporaryFolder testFolder = new TemporaryFolder();

  @Test
  public void testIndexing() throws IOException {

    LuceneIndexer indexer = new LuceneIndexer();

    indexer.closeSpellChecker();
    indexer.closeIndexReader();

    File indexDir = testFolder.newFolder(INDEX_DIR);
    File spellingDir = testFolder.newFolder(SPELLING_DIR);

    indexer.setIndexDir(indexDir.getAbsolutePath());
    indexer.setSpellingDir(spellingDir.getAbsolutePath());

    assertEquals(indexDir.getAbsolutePath(), indexer.getIndexDir());
    assertEquals(spellingDir.getAbsolutePath(), indexer.getSpellingDir());

    indexer.openIndexWriter();

    Entry entry = new Entry();
    entry.setLang(Language.hu);
    entry.setId("1");
    entry.setRoots(new HashSet<String>(Arrays.asList(new String[] {"aaaaaa", "aaaaab"})));
    entry.setForms(new HashSet<String>(Arrays.asList(new String[] {"bbbbbb"})));
    entry.setQuote(new HashSet<String>(Arrays.asList(new String[] {"cccccc"})));
    indexer.write(entry);

    entry = new Entry();
    entry.setLang(Language.no);
    entry.setId("2");
    entry.setRoots(new HashSet<String>(Arrays.asList(new String[] {"aaaaab", "aaaaac"})));
    entry.setForms(new HashSet<String>(Arrays.asList(new String[] {"bbbbbb"})));
    entry.setQuote(new HashSet<String>(Arrays.asList(new String[] {"cccccc"})));
    indexer.write(entry);

    indexer.closeIndexWriter();

    indexer.openIndexReader();
    indexer.openSpellChecker();

    indexer.createSuggestions();

    indexer.closeSpellChecker();
    indexer.closeIndexReader();

  }

}
