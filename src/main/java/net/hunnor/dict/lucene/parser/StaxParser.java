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
public class StaxParser {

	/**
	 * Default logger.
	 */
	private static final Logger LOGGER =
			LoggerFactory.getLogger(StaxParser.class);
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
		try (FileInputStream stream = new FileInputStream(file)) {
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
		Entry entry = new Entry();
		String element;
		StringBuilder characters = new StringBuilder();
		boolean getText = false;
		boolean isEg = false;
		boolean regularInflection = false;
		boolean primaryForm = false;
		String previous = "";
		StringBuilder text = new StringBuilder();
		StringBuilder formBuffer = new StringBuilder();
		StringBuilder inflParBuffer = new StringBuilder();
		StringBuilder inflSeqBuffer = new StringBuilder();
		int senseGrpCount = 0;
		StringBuilder senseGrpBuffer = new StringBuilder();
		int senseCount = 0;
		StringBuilder senseBuffer = new StringBuilder();
		StringBuilder egBuffer = new StringBuilder();

		while (reader.hasNext()) {
			int eventType = reader.next();
			switch (eventType) {
			case XMLEvent.START_ELEMENT:
				element = reader.getName().toString();
				if ((TagNames.ENTRY).equals(element)) {
					entry = new Entry();
					entry.setId(reader
							.getAttributeValue(null, "id"));
					text = new StringBuilder();
					senseGrpBuffer = new StringBuilder();
				} else if (("formGrp").equals(element)) {
					formBuffer = new StringBuilder();
				} else if ((TagNames.FORM).equals(element)) {
					regularInflection = false;
					inflParBuffer = new StringBuilder();
					primaryForm = "yes".equals(reader
							.getAttributeValue(null, "primary"));
				} else if ((TagNames.INFL_CODE).equals(element)
						&& "suff".equals(reader
								.getAttributeValue(null, "type"))) {
					getText = true;
					regularInflection = true;
				} else if ((TagNames.INFL_PAR).equals(element)) {
					if (inflParBuffer.length() > 0) {
						inflParBuffer.append("; ");
					}
					inflSeqBuffer = new StringBuilder();
				} else if ((TagNames.SENSE_GRP).equals(element)) {
					senseBuffer = new StringBuilder();
					if (senseGrpCount > 0) {
						if (senseGrpCount == 1) {
							senseGrpBuffer.insert(0, "<b>I</b> ");
						}
						senseGrpBuffer.append(
								" <b>"
								+ RomanNumerals.roman(senseGrpCount + 1)
								+ "</b> ");
					}
				} else if ((TagNames.SENSE).equals(element)) {
					if (senseCount > 0) {
						if (senseCount == 1) {
							senseBuffer.insert(0, "<b>1</b> ");
						}
						senseBuffer.append(
								" <b>" + (senseCount + 1) + "</b> ");
					}
				} else if (isTextNode(element)) {
					getText = true;
				} else if ((TagNames.EG).equals(element)) {
					egBuffer = new StringBuilder();
					isEg = true;
				}
			break;
			case XMLEvent.CHARACTERS:
				if (getText) {
					characters.append(reader.getText());
				}
			break;
			case XMLEvent.END_ELEMENT:
				element = reader.getName().toString();
				if ((TagNames.ENTRY).equals(element)) {
					text.append(formBuffer).append(" ").append(senseGrpBuffer);
					entry.setText(text.toString());
					return entry;
				} else if ((TagNames.FORM).equals(element)) {
					if (!regularInflection && inflParBuffer.length() > 0) {
						formBuffer.append(" (" + inflParBuffer + ")");
					}
				} else if ((TagNames.ORTH).equals(element)) {
					entry.getRoots().add(characters.toString());
					if (formBuffer.length() > 0) {
						formBuffer.append(" ");
					}
					formBuffer.append("<b>" + characters.toString() + "</b>");
					getText = false;
				} else if ((TagNames.POS).equals(element)) {
					if (primaryForm) {
						formBuffer.append(" " + characters.toString());
					}
					getText = false;
				} else if ((TagNames.INFL_CODE).equals(element)) {
					if (characters.length() > 0) {
						formBuffer.append(" " + characters.toString());
					}
					getText = false;
				} else if ((TagNames.INFL_PAR).equals(element)) {
					if (inflParBuffer.length() > 0) {
						inflParBuffer.append("; ");
					}
					inflParBuffer.append(inflSeqBuffer);
					getText = false;
				} else if ((TagNames.INFL_SEQ).equals(element)) {
						entry.getForms().add(characters.toString());
					if (inflSeqBuffer.length() > 0) {
						inflSeqBuffer.append(", ");
					}
					inflSeqBuffer.append(characters.toString());
					getText = false;
				} else if ((TagNames.SENSE_GRP).equals(element)) {
					senseGrpBuffer.append(senseBuffer);
					senseGrpCount++;
					senseCount = 0;
				} else if ((TagNames.SENSE).equals(element)) {
					senseCount++;
				} else if ((TagNames.TRANS).equals(element)) {
					if (isEg) {
						entry.getQuoteTrans().add(characters.toString());
						if (TagNames.TRANS.equals(previous)) {
							egBuffer.append(", ");
						} else if (TagNames.LBL.equals(previous)
								|| TagNames.Q.equals(previous)) {
							egBuffer.append(" ");
						}
						egBuffer.append(characters.toString());
					} else {
						entry.getTrans().add(characters.toString());
						if (TagNames.TRANS.equals(previous)) {
							senseBuffer.append(", ");
						} else if (TagNames.LBL.equals(previous)) {
							senseBuffer.append(" ");
						} else if (TagNames.EG.equals(previous)) {
							senseBuffer.append("; ");
						}
						senseBuffer.append(characters.toString());
					}
					getText = false;
				} else if ((TagNames.LBL).equals(element)) {
					if (senseBuffer.length() > 0) {
						senseBuffer.append(getGlue(element, previous));
					}
					senseBuffer.append(
							"<i>" + characters.toString() + "</i>");
					getText = false;
				} else if ((TagNames.EG).equals(element)) {
					isEg = false;
					if (senseBuffer.length() > 0) {
						senseBuffer.append(getGlue(element, previous));
					}
					senseBuffer.append(egBuffer);
				} else if ((TagNames.Q).equals(element)) {
					entry.getQuote().add(characters.toString());
					getText = false;
					if (egBuffer.length() > 0) {
						egBuffer.append(", ");
					}
					egBuffer.append(
							"<b>" + characters.toString() + "</b>");
				}
				previous = element;
				characters = new StringBuilder();
			break;
			default:
			break;
			}

		}
		return null;
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
