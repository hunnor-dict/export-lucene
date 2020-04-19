package net.hunnor.dict.lucene;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AntTaskTest {

  @Test
  void testLanguage() {
    AntTask task = new AntTask();
    task.setLanguage("language");
    assertEquals("language", task.getLanguage());
  }

  @Test
  void testMain() {
    AntTask task = new AntTask();
    task.setMain("main");
    assertEquals("main", task.getMain());
  }

  @Test
  void testSource() {
    AntTask task = new AntTask();
    task.setSource("source");
    assertEquals("source", task.getSource());
  }

  @Test
  void testExecute(@TempDir File tempDir) throws IOException {

    File xmlFile = new File("src/test/resources/xml/sample-entry-entry.xml");
    File indexDir = new File(tempDir, "index");

    AntTask task = new AntTask();

    task.setLanguage("HU");
    task.setMain(indexDir.getAbsolutePath());
    task.setSource(xmlFile.getAbsolutePath());

    task.execute();

    File[] indexFiles = indexDir.listFiles();
    assertTrue(indexFiles.length > 0);

  }

}
