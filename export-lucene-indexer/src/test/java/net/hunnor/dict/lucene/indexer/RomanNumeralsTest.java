package net.hunnor.dict.lucene.indexer;

import static org.junit.Assert.assertEquals;

import net.hunnor.dict.lucene.indexer.RomanNumerals;

import org.junit.Test;

public class RomanNumeralsTest {

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidNumber() {
    RomanNumerals.roman(0);
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
