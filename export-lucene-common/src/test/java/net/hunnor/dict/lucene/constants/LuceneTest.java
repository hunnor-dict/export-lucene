package net.hunnor.dict.lucene.constants;

import static org.junit.Assert.assertEquals;

import org.apache.lucene.util.Version;
import org.junit.Test;

public class LuceneTest {

  @Test
  public void versionTest() {
    assertEquals(Version.LUCENE_36, Lucene.VERSION);
  }

}
