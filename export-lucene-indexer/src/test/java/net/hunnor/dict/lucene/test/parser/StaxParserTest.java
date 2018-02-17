package net.hunnor.dict.lucene.test.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.hunnor.dict.lucene.model.Entry;
import net.hunnor.dict.lucene.parser.StaxParser;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import javax.xml.stream.XMLStreamException;

public class StaxParserTest {

  @Test
  public void testFileNotFoundException() throws XMLStreamException {
    StaxParser staxParser = new StaxParser();
    staxParser.openFile("src/test/resources/xml/no-such-file.xml");
  }

  @Test(expected = XMLStreamException.class)
  public void testXmlStreamException() throws XMLStreamException {
    StaxParser staxParser = new StaxParser();
    staxParser.openFile("src/test/resources/xml/sample-text-file.txt");
    staxParser.next();
  }

  @Test
  public void testEntryEntry() throws XMLStreamException {
    StaxParser staxParser = new StaxParser();
    assertFalse(staxParser.hasNext());
    staxParser.openFile("src/test/resources/xml/sample-entry-entry.xml");
    assertTrue(staxParser.hasNext());
    Entry entry = staxParser.next();
    assertEquals("1", entry.getId());
    assertTrue(staxParser.hasNext());
    entry = staxParser.next();
    assertEquals("2", entry.getId());
    assertTrue(staxParser.hasNext());
    entry = staxParser.next();
    assertFalse(staxParser.hasNext());
    staxParser.closeFile();
  }

  @Test
  public void testFormForm() throws XMLStreamException {
    StaxParser staxParser = new StaxParser();
    staxParser.openFile("src/test/resources/xml/sample-form-form.xml");
    Entry entry = staxParser.next();
    assertEquals("1", entry.getId());
    Set<String> roots = new HashSet<>();
    roots.add("orth1");
    roots.add("orth2");
    roots.add("orth3");
    assertEquals(roots, entry.getRoots());
    assertEquals(
        "<b>orth1</b> pos1 suff1 <b>orth2</b>"
            + " (inflSeq1, inflSeq2, inflSeq3, inflSeq4; ;"
            + " inflSeq5, inflSeq6) <b>orth3</b> ",
        entry.getText());
  }

  @Test
  public void testSenseSense() throws XMLStreamException {
    StaxParser staxParser = new StaxParser();
    staxParser.openFile("src/test/resources/xml/sample-sense-sense.xml");
    Entry entry = staxParser.next();
    assertEquals("1", entry.getId());
    Set<String> roots = new HashSet<>();
    assertEquals(roots, entry.getRoots());
    assertEquals(
        " <b>I</b> <b>1</b> trans1 <b>2</b> trans2 <b>II</b>" + " <b>1</b> trans3 <b>2</b> trans4",
        entry.getText());
  }

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

  @Test
  public void testEgEg() throws XMLStreamException {
    StaxParser staxParser = new StaxParser();
    staxParser.openFile("src/test/resources/xml/sample-eg-eg.xml");
    Entry entry = staxParser.next();
    assertEquals("1", entry.getId());
    Set<String> roots = new HashSet<>();
    assertEquals(roots, entry.getRoots());
    assertEquals(" <b>q1</b> trans1; trans2", entry.getText());
  }

  @Test
  public void testQuoteQuote() throws XMLStreamException {
    StaxParser staxParser = new StaxParser();
    staxParser.openFile("src/test/resources/xml/sample-q-q.xml");
    Entry entry = staxParser.next();
    assertEquals("1", entry.getId());
    Set<String> roots = new HashSet<>();
    assertEquals(roots, entry.getRoots());
    assertEquals(" <b>q1</b>, <b>q2</b> trans1", entry.getText());
  }

  @Test
  public void testQuoteTransTrans() throws XMLStreamException {
    StaxParser staxParser = new StaxParser();
    staxParser.openFile("src/test/resources/xml/sample-q-trans-trans.xml");
    Entry entry = staxParser.next();
    assertEquals("1", entry.getId());
    Set<String> roots = new HashSet<>();
    assertEquals(roots, entry.getRoots());
    assertEquals(" <b>q1</b> trans1, trans2", entry.getText());
  }

  @Test
  public void testQuoteLblTrans() throws XMLStreamException {
    StaxParser staxParser = new StaxParser();
    staxParser.openFile("src/test/resources/xml/sample-q-lbl-trans.xml");
    Entry entry = staxParser.next();
    assertEquals("1", entry.getId());
    Set<String> roots = new HashSet<>();
    assertEquals(roots, entry.getRoots());
    assertEquals(" <i>lbl1</i>; <b>q1</b> trans1", entry.getText());
  }

}