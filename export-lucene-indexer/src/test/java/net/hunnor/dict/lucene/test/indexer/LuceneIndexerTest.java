package net.hunnor.dict.lucene.test.indexer;

import net.hunnor.dict.lucene.indexer.LuceneIndexer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

public class LuceneIndexerTest {

  private static final String INDEX_DIR = "indexDir";

  private static final String SPELLING_DIR = "spellingDir";

  @Rule public TemporaryFolder testFolder = new TemporaryFolder();

  @Test
  public void testIndexing() throws IOException {

    LuceneIndexer indexer = new LuceneIndexer();

    File indexDir = testFolder.newFolder(INDEX_DIR);
    File spellingDir = testFolder.newFolder(SPELLING_DIR);

    indexer.setIndexDir(indexDir.getAbsolutePath());
    indexer.setSpellingDir(spellingDir.getAbsolutePath());

    indexer.openIndexWriter();
    indexer.closeIndexWriter();

    indexer.openSpellChecker();
    indexer.closeSpellChecker();

    indexer.openIndexReader();
    indexer.closeIndexReader();

  }

}
