package net.hunnor.dict.lucene;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

public class LauncherTest {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void testIncompleteArguments() {
    String[] args = new String[] {"-x", "file.xml"};
    Launcher.main(args);
    String[] files = temporaryFolder.getRoot().list();
    assertEquals(0, files.length);
  }

  @Test
  public void testInvalidArguments() {
    String[] args = new String[] {"-l", "h", "-x", "file.xml", "-d", "index", "-s", "speling"};
    Launcher.main(args);
    String[] files = temporaryFolder.getRoot().list();
    assertEquals(0, files.length);
  }

  @Test
  public void testValidArguments() throws IOException {

    File xmlFile = new File("src/test/resources/xml/sample-entry-entry.xml");
    File indexDir = temporaryFolder.newFolder("index");
    File spellingDir = temporaryFolder.newFolder("spelling");
    String[] args = new String[] {
        "-l", "HU",
        "-x", xmlFile.getAbsolutePath(),
        "-d", indexDir.getAbsolutePath(),
        "-s", spellingDir.getAbsolutePath()
    };
    Launcher.main(args);
    String[] files = temporaryFolder.getRoot().list();
    assertEquals(2, files.length);

    File index = new File(temporaryFolder.getRoot(), "index");
    assertTrue(index.isDirectory());
    files = index.list();
    assertTrue(files.length > 0);

    File spelling = new File(temporaryFolder.getRoot(), "spelling");
    assertTrue(spelling.isDirectory());
    files = spelling.list();
    assertTrue(files.length > 0);

  }

}
