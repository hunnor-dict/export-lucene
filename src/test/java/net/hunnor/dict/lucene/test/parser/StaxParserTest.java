	package net.hunnor.dict.lucene.test.parser;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;

import net.hunnor.dict.lucene.model.Entry;
import net.hunnor.dict.lucene.parser.StaxParser;

/**
 * Tests for XML parsing with StAX.
 */
public final class StaxParserTest {

	/**
	 * Test parsing of sample data.
	 * @throws XMLStreamException when thrown by the parser
	 */
	@Test
	public void testBil() throws XMLStreamException {
		StaxParser staxParser = new StaxParser();
		staxParser.openFile("src/test/resources/xml/sample-bil.xml");
		Entry entry = staxParser.next();
		assertEquals("1", entry.getId());
		Set<String> roots = new HashSet<>();
		roots.add("bil");
		assertEquals(roots, entry.getRoots());
		assertEquals("<b>bil</b> subst -en autó, <i>formal</i> személygépkocsi;"
				+ " <b>kjøre bil</b> autót vezet", entry.getText());
	}

	/**
	 * Test parsing of sample data.
	 * @throws XMLStreamException when thrown by the parser
	 */
	@Test
	public void testBonde() throws XMLStreamException {
		StaxParser staxParser = new StaxParser();
		staxParser.openFile("src/test/resources/xml/sample-bonde.xml");
		Entry entry = staxParser.next();
		assertEquals("2", entry.getId());
		Set<String> roots = new HashSet<>();
		roots.add("bonde");
		assertEquals(roots, entry.getRoots());
		assertEquals("<b>bonde</b> subst (bonden, bønder, bøndene) "
				+ "<b>1</b> paraszt <b>2</b> <i>sjakkbrikke</i> gyalog",
				entry.getText());
	}

	/**
	 * Test parsing of sample data.
	 * @throws XMLStreamException when thrown by the parser
	 */
	@Test
	public void testBlad() throws XMLStreamException {
		StaxParser staxParser = new StaxParser();
		staxParser.openFile("src/test/resources/xml/sample-blad.xml");
		Entry entry = staxParser.next();
		assertEquals("3", entry.getId());
		Set<String> roots = new HashSet<>();
		roots.add("blad");
		assertEquals(roots, entry.getRoots());
		assertEquals("<b>blad</b> subst -et, -/-er, -ene/-a "
				+ "<b>I</b> <b>1</b> levél <b>2</b> penge "
				+ "<b>II</b> lap; "
				+ "<b>spille fra bladet</b>, <b>synge fra bladet</b> felolvas",
				entry.getText());
	}

	/**
	 * Test parsing of sample data.
	 * @throws XMLStreamException when thrown by the parser
	 */
	@Test
	public void testBilLastebil() throws XMLStreamException {
		StaxParser staxParser = new StaxParser();
		staxParser.openFile("src/test/resources/xml/sample-bil-lastebil.xml");
		Entry entry = staxParser.next();
		assertEquals("1", entry.getId());
		Set<String> roots = new HashSet<>();
		roots.add("bil");
		assertEquals(roots, entry.getRoots());
		assertEquals("<b>bil</b> subst -en autó", entry.getText());
		entry = staxParser.next();
		assertEquals("2", entry.getId());
		roots = new HashSet<>();
		roots.add("lastebil");
		assertEquals(roots, entry.getRoots());
		assertEquals("<b>lastebil</b> subst -en teherautó", entry.getText());
	}

	/**
	 * Test parsing of sample data.
	 * @throws XMLStreamException when thrown by the parser
	 */
	@Test
	public void testFot() throws XMLStreamException {
		StaxParser staxParser = new StaxParser();
		staxParser.openFile("src/test/resources/xml/sample-fot.xml");
		Entry entry = staxParser.next();
		assertEquals("1", entry.getId());
		Set<String> roots = new HashSet<>();
		roots.add("fot");
		assertEquals(roots, entry.getRoots());
		assertEquals("<b>fot</b> subst <b>til fots</b> gyalog",
				entry.getText());
	}

}
