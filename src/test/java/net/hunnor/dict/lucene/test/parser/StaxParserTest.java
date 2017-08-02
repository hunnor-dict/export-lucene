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
	public void testLblLbl() throws XMLStreamException {
		StaxParser staxParser = new StaxParser();
		staxParser.openFile("src/test/resources/xml/sample-lbl-lbl.xml");
		Entry entry = staxParser.next();
		assertEquals("1", entry.getId());
		Set<String> roots = new HashSet<>();
		assertEquals(roots, entry.getRoots());
		assertEquals(" <i>lbl1</i> <i>lbl2</i>", entry.getText());
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
	public void testLblEg() throws XMLStreamException {
		StaxParser staxParser = new StaxParser();
		staxParser.openFile("src/test/resources/xml/sample-lbl-eg.xml");
		Entry entry = staxParser.next();
		assertEquals("1", entry.getId());
		Set<String> roots = new HashSet<>();
		assertEquals(roots, entry.getRoots());
		assertEquals(" <i>lbl1</i>; <b>q1</b> trans1", entry.getText());
	}

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
	public void testTransLbl() throws XMLStreamException {
		StaxParser staxParser = new StaxParser();
		staxParser.openFile("src/test/resources/xml/sample-trans-lbl.xml");
		Entry entry = staxParser.next();
		assertEquals("1", entry.getId());
		Set<String> roots = new HashSet<>();
		assertEquals(roots, entry.getRoots());
		assertEquals(" trans1, <i>lbl1</i>", entry.getText());
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
	public void testTransEg() throws XMLStreamException {
		StaxParser staxParser = new StaxParser();
		staxParser.openFile("src/test/resources/xml/sample-trans-eg.xml");
		Entry entry = staxParser.next();
		assertEquals("1", entry.getId());
		Set<String> roots = new HashSet<>();
		assertEquals(roots, entry.getRoots());
		assertEquals(" trans1; <b>q1</b> trans2", entry.getText());
	}

	/**
	 * Test parsing of sample data.
	 * @throws XMLStreamException when thrown by the parser
	 */
	@Test
	public void testEgEg() throws XMLStreamException {
		StaxParser staxParser = new StaxParser();
		staxParser.openFile("src/test/resources/xml/sample-eg-eg.xml");
		Entry entry = staxParser.next();
		assertEquals("1", entry.getId());
		Set<String> roots = new HashSet<>();
		assertEquals(roots, entry.getRoots());
		assertEquals(" <b>q1</b> trans1; <b>q2</b> trans2", entry.getText());
	}

	/**
	 * Test parsing of sample data.
	 * @throws XMLStreamException when thrown by the parser
	 */
	@Test
	public void testQQ() throws XMLStreamException {
		StaxParser staxParser = new StaxParser();
		staxParser.openFile("src/test/resources/xml/sample-q-q.xml");
		Entry entry = staxParser.next();
		assertEquals("1", entry.getId());
		Set<String> roots = new HashSet<>();
		assertEquals(roots, entry.getRoots());
		assertEquals(" <b>q1</b>, <b>q2</b> trans1", entry.getText());
	}

}
