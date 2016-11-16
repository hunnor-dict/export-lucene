package net.hunnor.dict.lucene.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.hunnor.dict.lucene.RomanNumerals;

/**
 * Test cases for conversion to Roman numerals.
 */
public final class RomanNumeralsTest {

	/**
	 * Integer for Roman numeral I.
	 */
	private static final int I = 1;

	/**
	 * Integer for Roman numeral MDCCLXXVI.
	 */
	private static final int MDCCLXXVI = 1776;

	/**
	 * Test conversion of 1 to I.
	 */
	@Test
	public void test1isI() {
		assertEquals("I", RomanNumerals.roman(I));
	}

	/**
	 * Test conversion of 1776 to MDCCLXXVI.
	 */
	@Test
	public void test1776isMDCCLXXVI() {
		assertEquals("MDCCLXXVI", RomanNumerals.roman(MDCCLXXVI));
	}

}
