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
	public void testTrans() throws XMLStreamException {
		StaxParser staxParser = new StaxParser();
		staxParser.openFile("src/test/resources/xml/sample-trans.xml");
		Entry entry = staxParser.next();
		assertEquals("1", entry.getId());
		Set<String> roots = new HashSet<>();
		assertEquals(roots, entry.getRoots());
		assertEquals(" trans1", entry.getText());
	}

	/**
	 * Test parsing of sample data.
	 * @throws XMLStreamException when thrown by the parser
	 */
	@Test
	public void testTransTrans() throws XMLStreamException {
		StaxParser staxParser = new StaxParser();
		staxParser.openFile("src/test/resources/xml/sample-trans-trans.xml");
		Entry entry = staxParser.next();
		assertEquals("1", entry.getId());
		Set<String> roots = new HashSet<>();
		assertEquals(roots, entry.getRoots());
		assertEquals(" trans1, trans2", entry.getText());
	}

	/**
	 * Test parsing of sample data.
	 * @throws XMLStreamException when thrown by the parser
	 */
	@Test
	public void testLblTrans() throws XMLStreamException {
		StaxParser staxParser = new StaxParser();
		staxParser.openFile("src/test/resources/xml/sample-lbl-trans.xml");
		Entry entry = staxParser.next();
		assertEquals("1", entry.getId());
		Set<String> roots = new HashSet<>();
		assertEquals(roots, entry.getRoots());
		assertEquals(" <i>lbl1</i> trans1", entry.getText());
	}

	/**
	 * Test parsing of sample data.
	 * @throws XMLStreamException when thrown by the parser
	 */
	@Test
	public void testTransLbl() throws XMLStreamException {
		StaxParser staxParser = new StaxParser();
		staxParser.openFile("src/test/resources/xml/sample-trans-lbl.xml");
		Entry entry = staxParser.next();
		assertEquals("1", entry.getId());
		Set<String> roots = new HashSet<>();
		assertEquals(roots, entry.getRoots());
		assertEquals(" trans1, <i>lbl1</i>", entry.getText());
	}

}
