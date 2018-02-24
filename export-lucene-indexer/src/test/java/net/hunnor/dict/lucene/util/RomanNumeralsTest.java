package net.hunnor.dict.lucene.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import net.hunnor.dict.lucene.util.RomanNumerals;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

public class RomanNumeralsTest {

  @Test
  public void testPrivateConstructor() throws Exception {
    Constructor<RomanNumerals> constructor = RomanNumerals.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    constructor.setAccessible(true);
    constructor.newInstance();
  }

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
