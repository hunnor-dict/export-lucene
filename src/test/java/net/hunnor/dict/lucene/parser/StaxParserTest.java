package net.hunnor.dict.lucene.parser;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;

import net.hunnor.dict.lucene.model.Entry;

public class StaxParserTest {

	@Test
	public void simpleCaseTest()
			throws FileNotFoundException, XMLStreamException {

		StaxParser staxParser = new StaxParser();
		staxParser.openFile("src/test/resources/xml/sample.nh.xml", "no");

		Entry entry = staxParser.next();
		assertEquals("1", entry.getId());
		Set<String> roots = new HashSet<>();
		roots.add("bil");
		assertEquals(roots, entry.getRoots());
		assertEquals("<b>bil</b> subst -en autó, <i>formal</i> személygépkocsi; <b>kjøre bil</b> autót vezet", entry.getText());

		entry = staxParser.next();
		assertEquals("<b>bonde</b> subst (bonden, bønder, bøndene) <b>1</b> paraszt <b>2</b> <i>sjakkbrikke</i> gyalog",
				entry.getText());

		entry = staxParser.next();
		assertEquals("<b>blad</b> subst -et, -/-er, -ene/-a <b>I</b> <b>1</b> levél <b>2</b> penge <b>II</b> lap",
				entry.getText());

	}

}
