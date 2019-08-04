package net.hunnor.dict.lucene;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class LauncherTest {

  @Test
  public void testIncompleteArguments(@TempDir File tempDir) {
    String[] args = new String[] {"-x", "file.xml"};
    Launcher.main(args);
    String[] files = tempDir.list();
    assertEquals(0, files.length);
  }

  @Test
  public void testInvalidArguments(@TempDir File tempDir) {
    String[] args = new String[] {"-l", "h", "-x", "file.xml", "-d", "index", "-s", "speling"};
    Launcher.main(args);
    String[] files = tempDir.list();
    assertEquals(0, files.length);
  }

  @Test
  public void testValidArguments(@TempDir File tempDir) throws IOException {

    File xmlFile = new File("src/test/resources/xml/sample-entry-entry.xml");
    File indexDir = new File(tempDir, "index");
    File spellingDir = new File(tempDir, "spelling");
    String[] args = new String[] {
        "-l", "HU",
        "-x", xmlFile.getAbsolutePath(),
        "-d", indexDir.getAbsolutePath(),
        "-s", spellingDir.getAbsolutePath()
    };
    Launcher.main(args);
    String[] files = tempDir.list();
    assertEquals(2, files.length);

    File index = new File(tempDir, "index");
    assertTrue(index.isDirectory());
    files = index.list();
    assertTrue(files.length > 0);

    File spelling = new File(tempDir, "spelling");
    assertTrue(spelling.isDirectory());
    files = spelling.list();
    assertTrue(files.length > 0);

  }

}
