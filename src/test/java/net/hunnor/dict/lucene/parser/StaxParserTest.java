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
	}

}
