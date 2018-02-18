package net.hunnor.dict.lucene.constants;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FieldNamesTest {

  @Test
  public void testIdField() {
    assertEquals("id", FieldNames.ID);
  }

}
