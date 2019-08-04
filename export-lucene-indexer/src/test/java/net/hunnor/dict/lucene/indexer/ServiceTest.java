package net.hunnor.dict.lucene.indexer;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import net.hunnor.dict.lucene.indexer.LuceneIndexer;
import net.hunnor.dict.lucene.indexer.Service;
import net.hunnor.dict.lucene.model.Language;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ServiceTest {

  @Test
  public void test(@TempDir File tempDir) throws IOException {
    Service service = new Service();
    File file = new File("src/test/resources/xml/sample-entry-entry.xml");
    assertTrue(file.isFile());
    File indexDir = new File(tempDir, "index");
    service.indexFile(file.getAbsolutePath(), Language.HU, indexDir.getAbsolutePath());
    File spellingDir = new File(tempDir, "spelling");
    service.indexSuggestions(indexDir.getAbsolutePath(), spellingDir.getAbsolutePath());
  }

  @Test
  public void testParserError(@TempDir File tempDir) throws XMLStreamException, IOException {
    StaxParser spyParser = spy(new StaxParser());
    doThrow(new XMLStreamException()).when(spyParser).hasNext();
    Service service = new Service();
    service.setParser(spyParser);
    File file = new File("src/test/resources/xml/sample-entry-entry.xml");
    assertTrue(file.isFile());
    File indexDir = new File(tempDir, "index");
    service.indexFile(file.getAbsolutePath(), Language.HU, indexDir.getAbsolutePath());
  }

  @Test
  public void testSpellingError(@TempDir File tempDir) throws IOException {
    LuceneIndexer spyIndexer = spy(new LuceneIndexer());
    doThrow(new IOException()).when(spyIndexer).openIndexReader();
    Service service = new Service();
    service.setIndexer(spyIndexer);
    File file = new File("src/test/resources/xml/sample-entry-entry.xml");
    assertTrue(file.isFile());
    File indexDir = new File(tempDir, "index");
    service.indexFile(file.getAbsolutePath(), Language.HU, indexDir.getAbsolutePath());
    File[] indexFiles = indexDir.listFiles();
    assertTrue(indexFiles.length > 0);
    File spellingDir = new File(tempDir, "spelling");
    service.indexSuggestions(indexDir.getAbsolutePath(), spellingDir.getAbsolutePath());
  }

}
