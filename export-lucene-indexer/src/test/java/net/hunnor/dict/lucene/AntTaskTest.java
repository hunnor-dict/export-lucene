package net.hunnor.dict.lucene;

import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

public class AntTaskTest {

  @Test
  public void testLanguage() {
    AntTask task = new AntTask();
    task.setLanguage("language");
  }

  @Test
  public void testMain() {
    AntTask task = new AntTask();
    task.setMain("main");
  }

  @Test
  public void testSource() {
    AntTask task = new AntTask();
    task.setSource("source");
  }

  @Test
  public void testSpelling() {
    AntTask task = new AntTask();
    task.setSpelling("spelling");
  }

  @Test
  public void testExecute() throws IOException {

    TemporaryFolder temporaryFolder = new TemporaryFolder();
    temporaryFolder.create();

    File xmlFile = new File("src/test/resources/xml/sample-entry-entry.xml");
    File indexDir = temporaryFolder.newFolder("index");
    File spellingDir = temporaryFolder.newFolder("spelling");

    AntTask task = new AntTask();

    task.setLanguage("HU");
    task.setMain(indexDir.getAbsolutePath());
    task.setSource(xmlFile.getAbsolutePath());
    task.setSpelling(spellingDir.getAbsolutePath());

    task.execute();

  }

}
