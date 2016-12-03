package net.hunnor.dict.lucene.parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.hunnor.dict.lucene.model.Entry;
import net.hunnor.dict.lucene.util.RomanNumerals;

/**
 * Parses an XML file and returns model objects.
 */
public final class StaxParser {

	/**
	 * Default logger.
	 */
	private static final Logger LOGGER =
			LoggerFactory.getLogger(StaxParser.class);

	/**
	 * Input stream for the XML file.
	 */
	private FileInputStream stream;

	/**
	 * Reader for the XML stream.
	 */
	private XMLStreamReader2 reader;

	/**
	 * Glue strings between elements.
	 */
	private static Map<String, String> glues;

	/**
	 * Names of elements with usable text.
	 */
	private static Set<String> textNodes;

	static {

		glues = new HashMap<>();
		glues.put(TagNames.TRANS + "2" + TagNames.LBL, ", ");
		glues.put(TagNames.LBL + "2" + TagNames.LBL, " ");
		glues.put(TagNames.EG + "2" + TagNames.LBL, "; ");
		glues.put(TagNames.TRANS + "2" + TagNames.EG, "; ");
		glues.put(TagNames.EG + "2" + TagNames.EG, "; ");
		glues.put(TagNames.LBL + "2" + TagNames.EG, " ");
		glues.put(TagNames.TRANS + "2" + TagNames.TRANS, ", ");
		glues.put(TagNames.LBL + "2" + TagNames.TRANS, " ");
		glues.put(TagNames.EG + "2" + TagNames.TRANS, "; ");

		textNodes = new HashSet<>();
		textNodes.add(TagNames.ORTH);
		textNodes.add(TagNames.POS);
		textNodes.add(TagNames.INFL_SEQ);
		textNodes.add(TagNames.TRANS);
		textNodes.add(TagNames.LBL);
		textNodes.add(TagNames.Q);

	}

	/**
	 * Open a stream from an XML file.
	 * @param file the file to open
	 */
	public void openFile(final String file) {
		try {
			stream = new FileInputStream(file);
			XMLInputFactory2 xmlInputFactory2 =
					(XMLInputFactory2) XMLInputFactory2.newInstance();
			xmlInputFactory2.setProperty(
					XMLInputFactory2.IS_NAMESPACE_AWARE, false);
			reader = (XMLStreamReader2) xmlInputFactory2
					.createXMLStreamReader(stream);
		} catch (IOException | XMLStreamException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * Close the stream of the XML file.
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	public void closeFile() {
		try {
			reader.close();
			stream.close();
		} catch (IOException | XMLStreamException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * Check if the parser has reached the end of the stream.
	 * The method returns true if there are events left in the
	 * stream. The events left might not contain any entries.
	 * @return true if the XML stream has events left, false otherwise
	 * @throws XMLStreamException 
	 */
	public boolean hasNext() throws XMLStreamException {
		return reader != null && reader.hasNext();
	}

	/**
	 * Returns the next entry from the stream, or null if the stream
	 * end before an entry can be constructed.
	 * @return an Entry with data from the stream, or null
	 * @throws XMLStreamException 
	 */
	public Entry next() throws XMLStreamException {
		ParserState parserState = new ParserState();
		while (reader.hasNext() && !parserState.isEntryComplete()) {
			int eventType = reader.next();
			switch (eventType) {
			case XMLEvent.START_ELEMENT:
				processStartElement(reader.getLocalName(), parserState);
				break;
			case XMLEvent.CHARACTERS:
				if (parserState.isCollectText()) {
					parserState.getCharacters().append(reader.getText());
				}
				break;
			case XMLEvent.END_ELEMENT:
				processEndElement(reader.getLocalName(), parserState);
				break;
			default:
				break;
			}
		}
		return parserState.getEntry();
	}

	/**
	 * Process start elements.
	 * @param element the element name
	 * @param parserState the parser state object
	 */
	private void processStartElement(
			final String element,
			final ParserState parserState) {
		processEntryStart(element, parserState);
		processFormGrpStart(element, parserState);
		processFormStart(element, parserState);
		processInflCodeStart(element, parserState);
		processInflParStart(element, parserState);
		processSenseGrpStart(element, parserState);
		processSenseStart(element, parserState);
		processEgStart(element, parserState);
		processStart(element, parserState);
	}

	/**
	 * Process end elements.
	 * @param element the element name
	 * @param parserState the parser state object
	 */
	private void processEndElement(
			final String element,
			final ParserState parserState) {
		processEntryEnd(element, parserState);
		processFormEnd(element, parserState);
		processOrthEnd(element, parserState);
		processPosEnd(element, parserState);
		processInflCodeEnd(element, parserState);
		processInflParEnd(element, parserState);
		processInflSeqEnd(element, parserState);
		processSenseGrpEnd(element, parserState);
		processSenseEnd(element, parserState);
		processTransEnd(element, parserState);
		processLblEnd(element, parserState);
		processEgEnd(element, parserState);
		processQEnd(element, parserState);
		processEnd(element, parserState);
	}

	/**
	 * Process start 'entry' elements.
	 * @param element the element name
	 * @param parserState the parser state object
	 */
	private void processEntryStart(
		final String element,
		final ParserState parserState) {
		if (!TagNames.ENTRY.equals(element)) {
			return;
		}
		Entry entry = new Entry();
		entry.setId(reader
				.getAttributeValue(null, "id"));
		parserState.setEntry(entry);
		parserState.setText(new StringBuilder());
		parserState.setSenseGrpBuilder(new StringBuilder());
	}

	/**
	 * Process start 'formGrp' elements.
	 * @param element the element name
	 * @param parserState the parser state object
	 */
	private void processFormGrpStart(
			final String element,
			final ParserState parserState) {
		if (!TagNames.FORM_GRP.equals(element)) {
			return;
		}
		parserState.setFormBuffer(new StringBuilder());
	}

	/**
	 * Process start 'form' elements.
	 * @param element the element name
	 * @param parserState the parser state object
	 */
	private void processFormStart(
			final String element,
			final ParserState parserState) {
		if (!TagNames.FORM.equals(element)) {
			return;
		}
		parserState.setRegularInflection(false);
		parserState.setInflParBuffer(new StringBuilder());
		parserState.setPrimaryForm(
				"yes".equals(reader.getAttributeValue(null, "primary")));
	}

	/**
	 * Process start 'inflCode' elements.
	 * @param element the element name
	 * @param parserState the parser state object
	 */
	private void processInflCodeStart(
			final String element,
			final ParserState parserState) {
		if (!TagNames.INFL_CODE.equals(element)) {
			return;
		}
		if ("suff".equals(reader.getAttributeValue(null, "type"))) {
			parserState.setCollectText(true);
			parserState.setRegularInflection(true);
		}
	}

	/**
	 * Process start 'inflPar' elements.
	 * @param element the element name
	 * @param parserState the parser state object
	 */
	private void processInflParStart(
			final String element,
			final ParserState parserState) {
		if (!TagNames.INFL_PAR.equals(element)) {
			return;
		}
		if (parserState.getInflParBuffer().length() > 0) {
			parserState.getInflParBuffer().append("; ");
			parserState.setInflSeqBuffer(new StringBuilder());
		}
	}

	/**
	 * Process start 'senseGrp' elements.
	 * @param element the element name
	 * @param parserState the parser state object
	 */
	private void processSenseGrpStart(
			final String element,
			final ParserState parserState) {
		if (!TagNames.SENSE_GRP.equals(element)) {
			return;
		}
		parserState.setSenseBuffer(new StringBuilder());
		if (parserState.getSenseGrpCount() > 0) {
			if (parserState.getSenseGrpCount() == 1) {
				parserState.getSenseGrpBuffer().insert(0, "<b>I</b> ");
			}
			parserState.getSenseGrpBuffer().append(" <b>" + RomanNumerals.roman(
					parserState.getSenseGrpCount() + 1) + "</b> ");
		}
	}

	/**
	 * Process start 'sense' elements.
	 * @param element the element name
	 * @param parserState the parser state object
	 */
	private void processSenseStart(
			final String element,
			final ParserState parserState) {
		if (!TagNames.SENSE.equals(element)) {
			return;
		}
		if (parserState.getSenseCount() > 0) {
			if (parserState.getSenseCount() == 1) {
				parserState.getSenseBuffer().insert(0, "<b>1</b> ");
			}
			parserState.getSenseBuffer().append(
					" <b>" + (parserState.getSenseCount() + 1) + "</b> ");
		}
	}

	/**
	 * Process start 'eg' elements.
	 * @param element the element name
	 * @param parserState the parser state object
	 */
	private void processEgStart(
			final String element,
			final ParserState parserState) {
		if (!TagNames.EG.equals(element)) {
			return;
		}
		parserState.setEgBuffer(new StringBuilder());
		parserState.setEg(true);
	}

	/**
	 * Process start elements.
	 * @param element the element name
	 * @param parserState the parser state object
	 */
	private void processStart(
			final String element,
			final ParserState parserState) {
		if (isTextNode(element)) {
			parserState.setCollectText(true);
		}
	}

	/**
	 * Process end 'entry' elements.
	 * @param element the element name
	 * @param parserState the parser state object
	 */
	private void processEntryEnd(
			final String element,
			final ParserState parserState) {
		if (!TagNames.ENTRY.equals(element)) {
			return;
		}
		parserState.getText()
				.append(parserState.getFormBuffer()).append(" ")
				.append(parserState.getSenseGrpBuffer());
		parserState.getEntry().setText(parserState.getText().toString());
		parserState.setEntryComplete(true);
	}

	/**
	 * Process end 'form' elements.
	 * @param element the element name
	 * @param parserState the parser state object
	 */
	private void processFormEnd(
			final String element,
			final ParserState parserState) {
		if (!TagNames.FORM.equals(element)) {
			return;
		}
		if (!parserState.isRegularInflection()
				&& parserState.getInflParBuffer().length() > 0) {
			parserState.getFormBuffer().append(
					" (" + parserState.getInflParBuffer() + ")");
		}
	}

	/**
	 * Process end 'orth' elements.
	 * @param element the element name
	 * @param parserState the parser state object
	 */
	private void processOrthEnd(
			final String element,
			final ParserState parserState) {
		if (!TagNames.ORTH.equals(element)) {
			return;
		}
		parserState.getEntry().getRoots()
				.add(parserState.getCharacters().toString());
		if (parserState.getFormBuffer().length() > 0) {
			parserState.getFormBuffer().append(" ");
		}
		parserState.getFormBuffer().append(
				"<b>" + parserState.getCharacters().toString() + "</b>");
		parserState.setCollectText(false);
	}

	/**
	 * Process end 'pos' elements.
	 * @param element the element name
	 * @param parserState the parser state object
	 */
	private void processPosEnd(
			final String element,
			final ParserState parserState) {
		if (!TagNames.POS.equals(element)) {
			return;
		}
		if (parserState.isPrimaryForm()) {
			parserState.getFormBuffer().append(
					" " + parserState.getCharacters().toString());
		}
		parserState.setCollectText(false);
	}

	/**
	 * Process end 'inflCode' elements.
	 * @param element the element name
	 * @param parserState the parser state object
	 */
	private void processInflCodeEnd(
			final String element,
			final ParserState parserState) {
		if (!TagNames.INFL_CODE.equals(element)) {
			return;
		}
		if (parserState.getCharacters().length() > 0) {
			parserState.getFormBuffer().append(
					" " + parserState.getCharacters().toString());
		}
		parserState.setCollectText(false);
	}

	/**
	 * Process end 'inflPar' elements.
	 * @param element the element name
	 * @param parserState the parser state object
	 */
	private void processInflParEnd(
			final String element,
			final ParserState parserState) {
		if (!TagNames.INFL_PAR.equals(element)) {
			return;
		}
		if (parserState.getInflParBuffer().length() > 0) {
			parserState.getInflParBuffer().append("; ");
		}
		parserState.getInflParBuffer()
				.append(parserState.getInflSeqBuffer());
		parserState.setCollectText(false);
	}

	/**
	 * Process end 'inflSeq' elements.
	 * @param element the element name
	 * @param parserState the parser state object
	 */
	private void processInflSeqEnd(
			final String element,
			final ParserState parserState) {
		if (!TagNames.INFL_SEQ.equals(element)) {
			return;
		}
		parserState.getEntry().getForms().add(
				parserState.getCharacters().toString());
		if (parserState.getInflSeqBuffer().length() > 0) {
			parserState.getInflSeqBuffer().append(", ");
		}
		parserState.getInflSeqBuffer().append(
				parserState.getCharacters().toString());
		parserState.setCollectText(false);
	}

	/**
	 * Process end 'senseGrp' elements.
	 * @param element the element name
	 * @param parserState the parser state object
	 */
	private void processSenseGrpEnd(
			final String element,
			final ParserState parserState) {
		if (!TagNames.SENSE_GRP.equals(element)) {
			return;
		}
		parserState.getSenseGrpBuffer()
				.append(parserState.getSenseBuffer());
		parserState.setSenseGrpCount(parserState.getSenseGrpCount() + 1);
		parserState.setSenseCount(0);
	}

	/**
	 * Process end 'sense' elements.
	 * @param element the element name
	 * @param parserState the parser state object
	 */
	private void processSenseEnd(
			final String element,
			final ParserState parserState) {
		if (!TagNames.SENSE.equals(element)) {
			return;
		}
		parserState.setSenseCount(parserState.getSenseCount() + 1);
	}

	/**
	 * Process end 'trans' elements.
	 * @param element the element name
	 * @param parserState the parser state object
	 */
	private void processTransEnd(
			final String element,
			final ParserState parserState) {
		if (!TagNames.TRANS.equals(element)) {
			return;
		}
		if (parserState.isEg()) {
			parserState.getEntry().getQuoteTrans().add(
					parserState.getCharacters().toString());
			if (TagNames.TRANS.equals(parserState.getPrevious())) {
				parserState.getEgBuffer().append(", ");
			} else if (TagNames.LBL.equals(parserState.getPrevious())
					|| TagNames.Q.equals(parserState.getPrevious())) {
				parserState.getEgBuffer().append(" ");
			}
			parserState.getEgBuffer().append(
					parserState.getCharacters().toString());
		} else {
			parserState.getEntry().getTrans().add(
					parserState.getCharacters().toString());
			parserState.getSenseBuffer().append(
					getGlue(element, parserState.getPrevious()));
			parserState.getSenseBuffer().append(
					parserState.getCharacters().toString());
		}
		parserState.setCollectText(false);
	}

	/**
	 * Process end 'lbl' elements.
	 * @param element the element name
	 * @param parserState the parser state object
	 */
	private void processLblEnd(
			final String element,
			final ParserState parserState) {
		if (!TagNames.LBL.equals(element)) {
			return;
		}
		if (parserState.getSenseBuffer().length() > 0) {
			parserState.getSenseBuffer().append(getGlue(
					element, parserState.getPrevious()));
		}
		parserState.getSenseBuffer().append(
				"<i>" + parserState.getCharacters().toString() + "</i>");
		parserState.setCollectText(false);
	}

	/**
	 * Process end 'eg' elements.
	 * @param element the element name
	 * @param parserState the parser state object
	 */
	private void processEgEnd(
			final String element,
			final ParserState parserState) {
		if (!TagNames.EG.equals(element)) {
			return;
		}
		parserState.setEg(false);
		if (parserState.getSenseBuffer().length() > 0) {
			parserState.getSenseBuffer().append(getGlue(
					element, parserState.getPrevious()));
		}
		parserState.getSenseBuffer().append(parserState.getEgBuffer());
	}

	/**
	 * Process end 'q' elements.
	 * @param element the element name
	 * @param parserState the parser state object
	 */
	private void processQEnd(
			final String element,
			final ParserState parserState) {
		if (!TagNames.Q.equals(element)) {
			return;
		}
		parserState.getEntry().getQuote()
				.add(parserState.getCharacters().toString());
		parserState.setCollectText(false);
		if (parserState.getEgBuffer().length() > 0) {
			parserState.getEgBuffer().append(", ");
		}
		parserState.getEgBuffer().append(
				"<b>" + parserState.getCharacters().toString() + "</b>");
	}

	/**
	 * Process end elements.
	 * @param element the element name
	 * @param parserState the parser state object
	 */
	private void processEnd(
			final String element,
			final ParserState parserState) {
		parserState.setPrevious(element);
		parserState.setCharacters(new StringBuilder());
	}

	/**
	 * Return the glue string for the specified combination of elements.
	 * @param element the name of the current element
	 * @param previous the name of the previous element
	 * @return the glue string for the specified combination
	 */
	private String getGlue(
			final String element,
			final String previous) {
		String glue = glues.get(previous + "2" + element);
		if (glue == null) {
			glue = "";
		}
		return glue;
	}

	/**
	 * Check if text should be collected for an element.
	 * @param elementName the name of the element
	 * @return if text should be collected from the element
	 */
	private boolean isTextNode(final String elementName) {
		return textNodes.contains(elementName);
	}

}
