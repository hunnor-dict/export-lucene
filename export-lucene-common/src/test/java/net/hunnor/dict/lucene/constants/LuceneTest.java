package net.hunnor.dict.lucene.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.lucene.util.Version;
import org.junit.jupiter.api.Test;

class LuceneTest {

  @Test
  void versionTest() {
    assertEquals(Version.LUCENE_4_10_4, Lucene.VERSION);
  }

}
