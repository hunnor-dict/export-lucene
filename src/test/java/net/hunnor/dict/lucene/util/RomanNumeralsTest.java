package net.hunnor.dict.lucene.util;

import static org.junit.Assert.*;

import org.junit.Test;

import net.hunnor.dict.lucene.util.RomanNumerals;

/**
 * Test cases for conversion to Roman numerals.
 */
public final class RomanNumeralsTest {

	/**
	 * Test conversion of 1 to I.
	 */
	@Test
	public void test1isI() {
		assertEquals("I", RomanNumerals.roman(1));
	}

	/**
	 * Test conversion of 1776 to MDCCLXXVI.
	 */
	@Test
	public void test1776isMDCCLXXVI() {
		assertEquals("MDCCLXXVI", RomanNumerals.roman(1776));
	}

}
