package net.hunnor.dict.lucene.test.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Test;

import net.hunnor.dict.lucene.util.RomanNumerals;

/**
 * Test cases for conversion to Roman numerals.
 */
public final class RomanNumeralsTest {

	/**
	 * Constant for Roman numeral I.
	 */
	private static final int I = 1;

	/**
	 * Constant for Roman numeral MDCCLXXVI.
	 */
	private static final int MDCCLXXVI = 1776;

	/**
	 * Test if the default constructor is private.
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@Test
	public void testPrivateConstructor() throws NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Constructor<RomanNumerals> constructor =
				RomanNumerals.class.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		constructor.setAccessible(true);
		constructor.newInstance();
	}

	/**
	 * Test if zero or a negative number throws an exception.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testInvalidNumber() {
		RomanNumerals.roman(0);
	}

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
