package net.hunnor.dict.lucene.indexer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import net.hunnor.dict.lucene.indexer.RomanNumerals;
import org.junit.jupiter.api.Test;

public class RomanNumeralsTest {

  @Test
  public void testInvalidNumber() {
    assertThrows(IllegalArgumentException.class, () -> {
      RomanNumerals.roman(0);
    });
  }

  @Test
  public void test1isI() {
    assertEquals("I", RomanNumerals.roman(1));
  }

  @Test
  public void test1776isMdcclxxvi() {
    assertEquals("MDCCLXXVI", RomanNumerals.roman(1776));
  }

}
