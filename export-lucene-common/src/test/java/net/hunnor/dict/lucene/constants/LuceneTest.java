package net.hunnor.dict.lucene.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.lucene.util.Version;
import org.junit.jupiter.api.Test;

public class LuceneTest {

  @Test
  public void versionTest() {
    assertEquals(Version.LUCENE_36, Lucene.VERSION);
  }

}
