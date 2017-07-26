package net.hunnor.dict.lucene.test.indexer.analyzer;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.Test;

import net.hunnor.dict.lucene.indexer.analyzer.PerFieldAnalyzer;

/**
 * Tests for the custom Lucene analyzer.
 */
public final class PerFieldAnalyzerTest {

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
		Constructor<PerFieldAnalyzer> constructor =
				PerFieldAnalyzer.class.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		constructor.setAccessible(true);
		constructor.newInstance();
	}

}
