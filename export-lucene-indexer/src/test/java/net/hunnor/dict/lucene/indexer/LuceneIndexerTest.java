package net.hunnor.dict.lucene.indexer;

import static org.junit.Assert.assertEquals;

import net.hunnor.dict.lucene.indexer.LuceneIndexer;
import net.hunnor.dict.lucene.model.Entry;
import net.hunnor.dict.lucene.model.Language;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

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

    indexer.closeIndexReader();
    indexer.closeIndexWriter();
    indexer.closeSpellChecker();

    File indexDir = testFolder.newFolder(INDEX_DIR);
    File spellingDir = testFolder.newFolder(SPELLING_DIR);

    indexer.setIndexDir(indexDir.getAbsolutePath());
    indexer.setSpellingDir(spellingDir.getAbsolutePath());
    assertEquals(indexDir.getAbsolutePath(), indexer.getIndexDir());
    assertEquals(spellingDir.getAbsolutePath(), indexer.getSpellingDir());

    Entry entry1 = new Entry();
    entry1.setLang(Language.hu);
    entry1.setId("1");
    entry1.setRoots(new HashSet<String>(Arrays.asList(new String[] {"aaaaaa", "aaaaab"})));
    entry1.setForms(new HashSet<String>(Arrays.asList(new String[] {"bbbbbb"})));
    entry1.setQuote(new HashSet<String>(Arrays.asList(new String[] {"cccccc"})));
    entry1.setTrans(new HashSet<String>(Arrays.asList(new String[] {"dddddd"})));
    entry1.setQuoteTrans(new HashSet<String>(Arrays.asList(new String[] {"eeeeee"})));
    entry1.setText("ffffff");
    indexer.write(entry1);
    Entry entry2 = new Entry();
    entry2.setLang(Language.no);
    entry2.setId("2");
    entry2.setRoots(new HashSet<String>(Arrays.asList(new String[] {"aaaaab", "aaaaac"})));
    entry2.setForms(new HashSet<String>(Arrays.asList(new String[] {"bbbbbb"})));
    entry2.setQuote(new HashSet<String>(Arrays.asList(new String[] {"cccccc"})));
    entry2.setTrans(new HashSet<String>(Arrays.asList(new String[] {"dddddd"})));
    entry2.setQuoteTrans(new HashSet<String>(Arrays.asList(new String[] {"eeeeee"})));
    entry2.setText("ffffff");
    indexer.write(entry2);
    Entry entry0 = new Entry();
    indexer.write(entry0);

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

  }

}
