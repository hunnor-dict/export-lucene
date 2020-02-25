package net.hunnor.dict.lucene;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class AntTaskTest {

  @Test
  public void testLanguage() {
    AntTask task = new AntTask();
    task.setLanguage("language");
    assertEquals("language", task.getLanguage());
  }

  @Test
  public void testMain() {
    AntTask task = new AntTask();
    task.setMain("main");
    assertEquals("main", task.getMain());
  }

  @Test
  public void testSource() {
    AntTask task = new AntTask();
    task.setSource("source");
    assertEquals("source", task.getSource());
  }

  @Test
  public void testSpelling() {
    AntTask task = new AntTask();
    task.setSpelling("spelling");
    assertEquals("spelling", task.getSpelling());
  }

  @Test
  public void testExecute(@TempDir File tempDir) throws IOException {

    File xmlFile = new File("src/test/resources/xml/sample-entry-entry.xml");
    File indexDir = new File(tempDir, "index");
    File spellingDir = new File(tempDir, "spelling");

    AntTask task = new AntTask();

    task.setLanguage("HU");
    task.setMain(indexDir.getAbsolutePath());
    task.setSource(xmlFile.getAbsolutePath());
    task.setSpelling(spellingDir.getAbsolutePath());

    task.execute();

    File[] indexFiles = indexDir.listFiles();
    assertTrue(indexFiles.length > 0);
    File[] spellingFiles = spellingDir.listFiles();
    assertTrue(spellingFiles.length > 0);

  }

}
