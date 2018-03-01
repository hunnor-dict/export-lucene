package net.hunnor.dict.lucene;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

public class LauncherTest {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test(expected = InvalidArgumentsException.class)
  public void testIncompleteArguments() {
    String[] args = new String[] {"-x", "file.xml"};
    Launcher.main(args);
  }

  @Test(expected = InvalidArgumentsException.class)
  public void testInvalidArguments() {
    String[] args = new String[] {"-l", "h", "-x", "file.xml", "-d", "index", "-s", "speling"};
    Launcher.main(args);
  }

  @Test
  public void execValidArguments() throws IOException {
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
  }

}
