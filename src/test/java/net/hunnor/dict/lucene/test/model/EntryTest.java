package net.hunnor.dict.lucene.test.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import net.hunnor.dict.lucene.model.Entry;

/**
 * Tests for the model of dictionary entries.
 */
public final class EntryTest {

	/**
	 * Test getters and setters for the language.
	 */
	@Test
	public void testLanguage() {
		Entry entry = new Entry();
		entry.setLang("lang");
		assertEquals("lang", entry.getLang());
	}

	/**
	 * Test getters and setters for the ID.
	 */
	@Test
	public void testId() {
		Entry entry = new Entry();
		entry.setId("1");
		assertEquals("1", entry.getId());
	}

	/**
	 * Test getters and setters for root forms.
	 */
	@Test
	public void testRoots() {
		Entry entry = new Entry();
		Set<String> roots = new HashSet<>();
		roots.add("root");
		entry.setRoots(roots);
		assertEquals(1, entry.getRoots().size());
		assertTrue(entry.getRoots().contains("root"));
	}

	/**
	 * Test getters and setters for inflected forms.
	 */
	@Test
	public void testForms() {
		Entry entry = new Entry();
		Set<String> forms = new HashSet<>();
		forms.add("form");
		entry.setForms(forms);
		assertEquals(1, entry.getForms().size());
		assertTrue(entry.getForms().contains("form"));
	}

	/**
	 * Test getters and setters for translations.
	 */
	@Test
	public void testTrans() {
		Entry entry = new Entry();
		Set<String> trans = new HashSet<>();
		trans.add("trans");
		entry.setTrans(trans);
		assertEquals(1, entry.getTrans().size());
		assertTrue(entry.getTrans().contains("trans"));
	}

	/**
	 * Test getters and setters for usage examples.
	 */
	@Test
	public void testQuote() {
		Entry entry = new Entry();
		Set<String> quote = new HashSet<>();
		quote.add("quote");
		entry.setQuote(quote);
		assertEquals(1, entry.getQuote().size());
		assertTrue(entry.getQuote().contains("quote"));
	}

	/**
	 * Test getters and setters for translations of usage examples.
	 */
	@Test
	public void testQuoteTrans() {
		Entry entry = new Entry();
		Set<String> quoteTrans = new HashSet<>();
		quoteTrans.add("quoteTrans");
		entry.setQuoteTrans(quoteTrans);
		assertEquals(1, entry.getQuoteTrans().size());
		assertTrue(entry.getQuoteTrans().contains("quoteTrans"));
	}

	/**
	 * Test getters and setters for the HTML content.
	 */
	@Test
	public void testText() {
		Entry entry = new Entry();
		entry.setText("<html/>");
		assertEquals("<html/>", entry.getText());
	}

}
