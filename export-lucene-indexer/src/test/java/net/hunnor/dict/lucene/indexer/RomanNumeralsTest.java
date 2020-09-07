package net.hunnor.dict.lucene.indexer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class RomanNumeralsTest {

  @Test
  void testInvalidNumber() {
    assertThrows(IllegalArgumentException.class, () -> {
      RomanNumerals.roman(0);
    });
  }

  @Test
  void test1isI() {
    assertEquals("I", RomanNumerals.roman(1));
  }

  @Test
  void test1776isMdcclxxvi() {
    assertEquals("MDCCLXXVI", RomanNumerals.roman(1776));
  }

}
