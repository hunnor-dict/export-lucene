package net.hunnor.dict.lucene.indexer;

import net.hunnor.dict.lucene.indexer.LuceneIndexer;
import net.hunnor.dict.lucene.indexer.Service;
import net.hunnor.dict.lucene.indexer.StaxParser;
import net.hunnor.dict.lucene.model.Language;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.management.*")
@PrepareForTest(Service.class)
public class ServiceTest {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void test() throws IOException {
    Service service = new Service();
    File file = new File("src/test/resources/xml/sample-entry-entry.xml");
    File indexDir = temporaryFolder.newFolder("index");
    service.indexFile(file.getAbsolutePath(), Language.HU, indexDir.getAbsolutePath());
    File spellingDir = temporaryFolder.newFolder("spelling");
    service.indexSuggestions(indexDir.getAbsolutePath(), spellingDir.getAbsolutePath());
  }

  @Test
  public void testParserError() throws Exception {
    StaxParser spyParser = PowerMockito.spy(new StaxParser());
    PowerMockito.when(spyParser, "hasNext").thenThrow(new XMLStreamException());
    Service spyService = PowerMockito.spy(new Service());
    PowerMockito.when(spyService, "getParser").thenReturn(spyParser);
    File file = new File("src/test/resources/xml/sample-entry-entry.xml");
    File indexDir = temporaryFolder.newFolder("index");
    spyService.indexFile(file.getAbsolutePath(), Language.HU, indexDir.getAbsolutePath());
  }

  @Test
  public void testSpellingError() throws Exception {
    LuceneIndexer spyIndexer = PowerMockito.spy(new LuceneIndexer());
    PowerMockito.when(spyIndexer, "createSuggestions").thenThrow(new IOException());
    Service spyService = PowerMockito.spy(new Service());
    PowerMockito.when(spyService, "getIndexer").thenReturn(spyIndexer);
    File file = new File("src/test/resources/xml/sample-entry-entry.xml");
    File indexDir = temporaryFolder.newFolder("index");
    spyService.indexFile(file.getAbsolutePath(), Language.HU, indexDir.getAbsolutePath());
    File spellingDir = temporaryFolder.newFolder("spelling");
    spyService.indexSuggestions(indexDir.getAbsolutePath(), spellingDir.getAbsolutePath());
  }

}
