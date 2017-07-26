package net.hunnor.dict.lucene.test.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.hunnor.dict.lucene.model.Entry;

/**
 * Tests for the model of dictionary entries.
 */
public final class EntryTest {

	/**
	 * Test getters and setters for the language field.
	 */
	@Test
	public void testLanguage() {
		Entry entry = new Entry();
		entry.setLang("lang");
		assertEquals("lang", entry.getLang());
	}

	/**
	 * Test getters and setters for the ID field.
	 */
	@Test
	public void testId() {
		Entry entry = new Entry();
		entry.setId("1");
		assertEquals("1", entry.getId());
	}

}
