package net.hunnor.dict.lucene.test.indexer;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import net.hunnor.dict.lucene.indexer.LuceneIndexer;

/**
 * Test cases for handling Lucene indexing.
 */
public final class LuceneIndexerTest {

	/**
	 * The name of the temporary index directory.
	 */
	private static final String INDEX_DIR = "indexDir";

	/**
	 * The name of the temporary spell checking index directory.
	 */
	private static final String SPELLING_DIR = "spellingDir";

	/**
	 * Temporary folder for test output.
	 */
	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	/**
	 * Test setting directories.
	 * @throws IOException 
	 */
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
