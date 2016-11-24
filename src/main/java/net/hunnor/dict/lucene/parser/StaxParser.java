package net.hunnor.dict.lucene.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;

import net.hunnor.dict.lucene.model.Entry;
import net.hunnor.dict.lucene.util.RomanNumerals;

/**
 * Parses an XML file and returns model objects.
 */
public class StaxParser {

	/**
	 * Reader for the XML stream.
	 */
	private XMLStreamReader2 reader;

	/**
	 * Open a stream from an XML file.
	 * @param file the file to open
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	public void openFile(final String file)
			throws FileNotFoundException, XMLStreamException {
		XMLInputFactory2 xmlInputFactory2 =
				(XMLInputFactory2) XMLInputFactory2.newInstance();
		xmlInputFactory2.setProperty(
				XMLInputFactory2.IS_NAMESPACE_AWARE, false);
		reader = (XMLStreamReader2) xmlInputFactory2
				.createXMLStreamReader(new FileInputStream(file));
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
		int eventType;
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
			eventType = reader.next();
			switch (eventType) {
			case XMLEvent.START_ELEMENT:
				element = reader.getName().toString();
				if (("entry").equals(element)) {
					entry = new Entry();
					entry.setId(reader
							.getAttributeValue(null, "id"));
					text = new StringBuilder();
					senseGrpBuffer = new StringBuilder();
				} else if (("formGrp").equals(element)) {
					formBuffer = new StringBuilder();
				} else if (("form").equals(element)) {
					regularInflection = false;
					inflParBuffer = new StringBuilder();
					if ("yes".equals(reader
							.getAttributeValue(null, "primary"))) {
						primaryForm = true;
					} else {
						primaryForm = false;
					}
				} else if (("inflCode").equals(element)
						&& "suff".equals(reader
								.getAttributeValue(null, "type"))) {
					getText = true;
					regularInflection = true;
				} else if (("inflPar").equals(element)) {
					if (inflParBuffer.length() > 0) {
						inflParBuffer.append("; ");
					}
					inflSeqBuffer = new StringBuilder();
				} else if (("senseGrp").equals(element)) {
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
				} else if (("sense").equals(element)) {
					if (senseCount > 0) {
						if (senseCount == 1) {
							senseBuffer.insert(0, "<b>1</b> ");
						}
						senseBuffer.append(
								" <b>" + (senseCount + 1) + "</b> ");
					}
				} else if (("orth").equals(element)
						|| ("pos").equals(element)
						|| ("inflSeq").equals(element)
						|| ("trans").equals(element)
						|| ("lbl").equals(element)
						|| ("q").equals(element)) {
					getText = true;
				} else if (("eg").equals(element)) {
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
				if (("entry").equals(element)) {
					text.append(formBuffer).append(" ").append(senseGrpBuffer);
					entry.setText(text.toString());
					return entry;
				} else if (("form").equals(element)) {
					if (!regularInflection && inflParBuffer.length() > 0) {
						formBuffer.append(" (" + inflParBuffer + ")");
					}
				} else if (("orth").equals(element)) {
					entry.getRoots().add(characters.toString());
					if (formBuffer.length() > 0) {
						formBuffer.append(" ");
					}
					formBuffer.append("<b>" + characters.toString() + "</b>");
					getText = false;
				} else if (("pos").equals(element)) {
					if (primaryForm) {
						formBuffer.append(" " + characters.toString());
					}
					getText = false;
				} else if (("inflCode").equals(element)) {
					if (characters.length() > 0) {
						formBuffer.append(" " + characters.toString());
					}
					getText = false;
				} else if (("inflPar").equals(element)) {
					if (inflParBuffer.length() > 0) {
						inflParBuffer.append("; ");
					}
					inflParBuffer.append(inflSeqBuffer);
					getText = false;
				} else if (("inflSeq").equals(element)) {
						entry.getForms().add(characters.toString());
					if (inflSeqBuffer.length() > 0) {
						inflSeqBuffer.append(", ");
					}
					inflSeqBuffer.append(characters.toString());
					getText = false;
				} else if (("senseGrp").equals(element)) {
					senseGrpBuffer.append(senseBuffer);
					senseGrpCount++;
					senseCount = 0;
				} else if (("sense").equals(element)) {
					previous = "";
					senseCount++;
				} else if (("trans").equals(element)) {
					if (isEg) {
						entry.getQuoteTrans().add(characters.toString());
						if (TagNames.TRANS.equals(previous)) {
							egBuffer.append(", ");
						} else if ("lbl".equals(previous)
								|| "q".equals(previous)) {
							egBuffer.append(" ");
						}
						egBuffer.append(characters.toString());
					} else {
						entry.getTrans().add(characters.toString());
						if (TagNames.TRANS.equals(previous)) {
							senseBuffer.append(", ");
						} else if ("lbl".equals(previous)) {
							senseBuffer.append(" ");
						} else if ("eg".equals(previous)) {
							senseBuffer.append("; ");
						}
						senseBuffer.append(characters.toString());
					}
					previous = TagNames.TRANS;
					getText = false;
				} else if (("lbl").equals(element)) {
					if (senseBuffer.length() > 0) {
						if (TagNames.TRANS.equals(previous)) {
							senseBuffer.append(", ");
						} else if ("lbl".equals(previous)) {
							senseBuffer.append(" ");
						} else if ("eg".equals(previous)) {
							senseBuffer.append("; ");
						}
					}
					senseBuffer.append(
							"<i>" + characters.toString() + "</i>");
					previous = "lbl";
					getText = false;
				} else if (("eg").equals(element)) {
					isEg = false;
					previous = "eg";
					if (senseBuffer.length() > 0) {
						if (TagNames.TRANS.equals(previous)
								|| "eg".equals(previous)) {
							senseBuffer.append("; ");
						} else if ("lbl".equals(previous)) {
							senseBuffer.append(" ");
						}
					}
					senseBuffer.append(egBuffer);
				} else if (("q").equals(element)) {
					entry.getQuote().add(characters.toString());
					getText = false;
					if (egBuffer.length() > 0) {
						egBuffer.append(", ");
					}
					egBuffer.append(
							"<b>" + characters.toString() + "</b>");
					previous = "q";
				}
				characters = new StringBuilder();
			break;
			default:
			break;
			}
		}
		return null;
	}

}
